
SET hive.auto.convert.join=false;

SET hive.auto.convert.join.noconditionaltask=false;

SET mapreduce.job.ubertask.enable=true;
SET mapreduce.job.ubertask.maxmaps=9;
SET mapreduce.job.ubertask.maxreduces=2;

SELECT m.title, COUNT(r.rating) cnt FROM movies m
LEFT JOIN ratings r ON m.id = r.movieid
GROUP BY m.title
ORDER BY cnt DESC
LIMIT 20;
