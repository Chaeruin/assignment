local stock_key = "coupon:stock:" .. ARGV[1]
local user_key = "coupon:issued:user:" .. ARGV[1]
local total_quantity = tonumber(ARGV[3])

-- [중요] Redis에 데이터가 없으면 DB에서 가져온 수량으로 초기화 (최초 1회)
if redis.call("EXISTS", stock_key) == 0 then
    redis.call("SET", stock_key, total_quantity)
end

-- 1. 중복 발급 확인
if redis.call("SISMEMBER", user_key, user_id) == 1 then
    return -1 -- 중복 발급 에러 코드
end

-- 2. 재고 확인 (현재 재고가 0 이하인지 확인)
local current_stock = tonumber(redis.call("GET", stock_key))
if current_stock <= 0 then
    return -2 -- 에러: 재고 소진
end

-- 3. 원자적 차감 및 유저 등록 (DECR + SADD)
redis.call("DECR", stock_key)
redis.call("SADD", user_key, user_id)

return 1 -- 성공