local key =  KEYS[1]
local startcount = tonumber(ARGV[1])
local is_exists = redis.call("EXISTS", key)
if is_exists == 1 then
    redis.call("INCR", key)
	return redis.call("GET", key)
    end
else
    redis.call("SET", key, startcount)
    return startcount
end