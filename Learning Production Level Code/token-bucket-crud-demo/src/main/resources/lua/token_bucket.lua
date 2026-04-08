-- Token Bucket Rate Limiting Lua Script
-- 
-- Implements token bucket algorithm atomically in Redis.
-- Ensures thread-safe rate limiting across multiple application instances.
--
-- KEYS[1]: Redis key for the token bucket (e.g., "token_bucket:user123")
-- ARGV[1]: capacity - Maximum tokens in bucket
-- ARGV[2]: refill_rate - Tokens added per second
-- ARGV[3]: now - Current timestamp (epoch seconds)
-- ARGV[4]: requested - Number of tokens requested (typically 1)
--
-- Returns: 1 if request allowed, 0 if rate limit exceeded

local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

-- Get current bucket state: tokens remaining and last refill timestamp
local bucket = redis.call("HMGET", key, "tokens", "timestamp")

local tokens = tonumber(bucket[1])
local last_refill = tonumber(bucket[2])

-- Initialize bucket if it doesn't exist (first request)
if tokens == nil then
    tokens = capacity
    last_refill = now
end

-- Calculate tokens to add based on time elapsed since last refill
local delta = math.max(0, now - last_refill)
local refill = delta * refill_rate
tokens = math.min(capacity, tokens + refill)

-- Check if request can be allowed
local allowed = tokens >= requested

-- Consume token if allowed
if allowed then
    tokens = tokens - requested
end

-- Update bucket state in Redis
redis.call("HMSET", key,
    "tokens", tokens,
    "timestamp", now
)

-- Set TTL to 1 hour to prevent stale keys
redis.call("EXPIRE", key, 3600)

return allowed