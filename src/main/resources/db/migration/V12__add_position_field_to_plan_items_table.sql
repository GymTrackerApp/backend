ALTER TABLE plan_items 
ADD COLUMN position INTEGER;

UPDATE plan_items
SET position = subquery.new_pos
FROM (SELECT exercise_id, training_plan_id, row_number() over (partition by training_plan_id) - 1 as new_pos FROM plan_items) AS subquery
WHERE plan_items.exercise_id = subquery.exercise_id and plan_items.training_plan_id = subquery.training_plan_id;

ALTER TABLE plan_items
ALTER COLUMN position SET NOT NULL