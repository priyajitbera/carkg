# SQLs
#### schema
```
use cars;
```

#### car embedding
```
select c.id, e.vector from car c join embedding e on c.embedding_id = e.id;
select * from car order by id;
```

#### brand embedding
```
select b.id, e.vector from brand b join embedding e on b.embedding_id = e.id;
select * from brand order by id;
```