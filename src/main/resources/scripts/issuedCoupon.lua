local coupon_id = ARGV[1]
local user_id = ARGV[2]
local total_quantity = tonumber(ARGV[3])

local stock_key = "coupon:stock:" .. coupon_id
local user_key = "coupon:issued:user:" .. coupon_id

-- 1. 중복 발급 확인
if redis.call("SISMEMBER", user_key, user_id) == 1 then
    return -1 -- 중복 발급 에러 코드
end

-- 2. 수량 확인 및 차감
local current_stock = tonumber(redis.call("GET", stock_key) or "0")
if current_stock >= total_quantity then
    return -2 -- 재고 소진 에러 코드
end

-- 3. 발급 기록
redis.call("INCR", stock_key)
redis.call("SADD", user_key, user_id)

return 1 -- 성공