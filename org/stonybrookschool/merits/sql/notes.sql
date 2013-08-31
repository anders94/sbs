select concat( u.first, ' ', u.last ) as name, g.name as dorm from users u, groups g, users_groups ug where ug.userId = u.id and ug.groupId = g.id;

-- set up groups
insert into users_groups (userId, groupId) select u.id as userId, g.id as groupId from users u, groups g, dorms d where g.name = 'hegeman' and u.dormId = d.id and d.name='hegeman';
