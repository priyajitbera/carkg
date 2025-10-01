user cars;

-- Drop if already exists
DROP FUNCTION IF EXISTS cosine_similarity;

DELIMITER $$

CREATE FUNCTION cosine_similarity(vec1 JSON, vec2 JSON)
RETURNS DOUBLE
DETERMINISTIC
BEGIN
    DECLARE dot_product DOUBLE DEFAULT 0.0;
    DECLARE norm1 DOUBLE DEFAULT 0.0;
    DECLARE norm2 DOUBLE DEFAULT 0.0;
    DECLARE i INT DEFAULT 0;
    DECLARE len INT;

    SET len = JSON_LENGTH(vec1);

    IF len != JSON_LENGTH(vec2) OR len = 0 THEN
        RETURN NULL; -- Vectors must be non-empty and equal length
    END IF;

    WHILE i < len DO
        SET dot_product = dot_product +
            (CAST(JSON_EXTRACT(vec1, CONCAT('$[', i, ']')) AS DECIMAL(20,10)) *
             CAST(JSON_EXTRACT(vec2, CONCAT('$[', i, ']')) AS DECIMAL(20,10)));

        SET norm1 = norm1 +
            POW(CAST(JSON_EXTRACT(vec1, CONCAT('$[', i, ']')) AS DECIMAL(20,10)), 2);

        SET norm2 = norm2 +
            POW(CAST(JSON_EXTRACT(vec2, CONCAT('$[', i, ']')) AS DECIMAL(20,10)), 2);

        SET i = i + 1;
    END WHILE;

    IF norm1 = 0 OR norm2 = 0 THEN
        RETURN NULL; -- Prevent divide by zero
    END IF;

    RETURN dot_product / (SQRT(norm1) * SQRT(norm2));
END$$

DELIMITER ;