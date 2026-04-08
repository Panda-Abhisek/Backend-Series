local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

local bucket = redis.call("HMGET", key, "tokens", "timestamp")

local tokens = tonumber(bucket[1])
local last_refill = tonumber(bucket[2])

if tokens == nil then
    tokens = capacity
    last_refill = now
end

local delta = math.max(0, now - last_refill)
local refill = delta * refill_rate
tokens = math.min(capacity, tokens + refill)

local allowed = tokens >= requested

if allowed then
    tokens = tokens - requested
end

redis.call("HMSET", key,
    "tokens", tokens,
    "timestamp", now
)

redis.call("EXPIRE", key, 3600)

return allowed