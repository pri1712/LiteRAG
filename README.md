LiteRAG is a memory-efficient document retrieval system built for scenarios where resources are limited. Unlike traditional RAG systems requiring 4-8GB RAM, LiteRAG delivers 74% recall using only 512MB heap space through optimized BM25 lexical search, along with some custom scoring.

Perfect for Raspberry Pi, old laptops, embedded systems, personal knowledge bases, and cost-optimized cloud deployments.

**Why RAG-Lite?**
The Problem
Modern RAG systems are resource-hungry:

Elasticsearch: 2GB+ RAM minimum
FAISS with embeddings: 4-8GB RAM
Vector databases: Heavy dependencies and setup complexity

The Solution
RAG-Lite uses pure BM25 lexical search with smart optimizations:

512MB RAM for 100K documents
Zero external dependencies (just Java 17+)
30-second setup from download to first search
74% recall@15 on standard benchmarks (SQUAD 2.0)  
