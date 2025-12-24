# LiteSearch Design Document

## Core Philosophy

LiteSearch is a **batch-built, immutable retrieval engine** designed for predictability, low memory overhead, and inspectable behavior.

## Architecture Model

### Two-Phase Operation

LiteSearch operates in two **disjoint phases**:

#### 1. Build Phase (Offline)
- Index is constructed from source documents
- Written to disk in immutable segments
- Can be computationally expensive
- Happens outside serving path

#### 2. Serve Phase (Online)
- Index is completely immutable
- All queries are read-only operations
- No writes, updates, or deletes
- Predictable memory and latency behavior

**Critical invariant:** These phases never overlap. No concurrent writes during serving.

### Why Immutability?

Immutable indexes provide:
- **Predictable memory**: Exact memory bounds can be precomputed
- **Deterministic IO**: No write amplification, no compaction during queries
- **Simple crash semantics**: No partial writes, no corruption recovery
- **Cache-friendly**: mmap works safely, no invalidation complexity
- **Concurrent queries**: Lock-free reads without coordination


## Current State (v0.1.0-alpha)

### What Exists Today
- Single monolithic index built offline
- BM25 ranking with length normalization
- Delta-encoded postings lists
- Custom disk layout for sequential/random access
- Read-only query execution

### What Works
- Benchmarked on SQuAD dataset
- Recall@K measurements
- Basic query performance metrics

### Known Limitations
- Single segment (not multi-segment architecture yet)
- No index versioning or atomic swaps
- Limited observability
- No formal memory accounting

## Segment Architecture (Target Design)

### Conceptual Model

```
Index = {Segment₁, Segment₂, ..., Segmentₙ}

Each segment:
  - Immutable after creation
  - Independently queryable
  - Contains subset of documents
  - Self-contained (postings + metadata)
```

### Why Segments?

Even with immutable indexes, segments enable:
- **Bounded memory**: Each segment has fixed size
- **Incremental updates**: Add new segments without rebuilding everything
- **Compaction**: Merge small segments offline
- **Sharding foundation**: Segments are natural distribution units
- **Cache management**: LRU eviction at segment granularity

### Query Execution Over Segments

```
Query(q) = Merge(
  Segment₁.Query(q),
  Segment₂.Query(q),
  ...
  Segmentₙ.Query(q)
)
```

**Performance questions to answer:**
- How does p99 latency scale with segment count?
- What's the tail latency amplification factor?
- Can we cache segment-level results safely?
- When does fan-out dominate query time?

## Incremental Updates (Without Live Writes)

### The Pattern: Atomic Index Swap

Instead of online writes, we use **versioned index replacement**:

```
1. Build new index version offline (V₂)
2. V₁ continues serving during build
3. Atomically swap active version pointer: V₁ → V₂
4. Gracefully drain V₁ queries
5. Drop V₁ after drain
```

### Guarantees
- Zero-downtime updates
- No query sees partial index state
- Crash during build → V₁ keeps serving
- Crash during swap → atomicity via pointer update

### Trade-offs
- **Pro**: Simple semantics, no write complexity
- **Pro**: Build can be fully optimized without serving constraints
- **Con**: Update latency is build time (minutes, not seconds)
- **Con**: Not suitable for sub-second freshness requirements


## Memory Model

### Serve Phase Memory Budget

```
Total Memory = 
  Segment₁.size + ... + Segmentₙ.size  (mmap'd)
  + Query cache (configurable)
  + Active query state (bounded by concurrency)
```

### Design Goals
- **Predictable**: Can precompute exact memory for N documents
- **Bounded**: No unbounded allocations on query path
- **Inspectable**: Expose memory breakdown via API

### Target Envelopes (To Be Measured)
- 1M documents → X MB RAM
- 10M documents → Y MB RAM
- 100M documents → Z MB RAM

Each with p99 query latency.

## Failure Semantics

### Build Phase Failures
- **Crash during build**: No impact on serving (old index still valid)
- **Corrupted segment write**: Detected via checksums, build fails safely
- **Partial segment**: Atomic write of segment metadata → all-or-nothing

### Serve Phase Failures
- **Query crash**: No impact on index (immutable)
- **Segment read error**: Return error, other segments unaffected
- **Memory exhaustion**: Fail gracefully, don't corrupt index

### Crash Recovery
- Index is always in consistent state (write-once semantics)
- No WAL needed
- No replay needed
- Restart → mmap → serve

## Observability

### Metrics Exposed
- **Query-level**: Latency histogram (p50/p95/p99), throughput
- **Segment-level**: Hit count per segment, cache hit rate
- **Memory**: RSS, mmap'd bytes, cache size
- **IO**: Bytes read, seeks, sequential vs random

### Tracing (Optional)
- Per-query trace showing segment fan-out
- Postings list access patterns
- Ranking computation breakdown


## What This Design Explicitly Does NOT Support

- Real-time writes during serving
- Document updates or deletes without rebuild
- Sub-second index freshness
- Transactions or ACID semantics
- Distributed coordination (yet)


## Comparison to Existing Systems

### vs Lucene
- **Lucene**: Dynamic segments, concurrent writes, complex compaction
- **LiteSearch**: Static segments, offline builds, atomic swaps
- **Tradeoff**: We sacrifice write latency for operational simplicity

### vs Elasticsearch
- **ES**: Distributed, highly available, real-time indexing
- **LiteSearch**: Single-node, immutable, batch-optimized
- **Tradeoff**: We sacrifice scale-out for predictability

### vs Tantivy
- **Tantivy**: High-performance Rust search, similar segment model
- **LiteSearch**: Educational Java implementation, explicit constraints
- **Tradeoff**: Performance for clarity and learnability

## Success Criteria (v0.2)

LiteSearch will be "complete" at v0.2 when:

1. **Formal segment model**: Multiple segments queryable
2. **Atomic index swap**: Zero-downtime updates demonstrated
3. **Memory accounting**: Exact memory usage for X documents
4. **Performance envelope**: Clear p99 latency bounds documented
5. **Observability**: Metrics and tracing operational
6. **Write-up**: "Why LiteSearch is intentionally immutable" published


## Future Considerations (Post v0.2)

Only after completing v0.2:
- **Segment compaction**: Merge small segments offline
- **Distributed segments**: Static sharding across nodes
- **Advanced compression**: PForDelta, Elias-Fano encodings
- **Query optimization**: Skip lists, early termination

None of these require abandoning immutability.

