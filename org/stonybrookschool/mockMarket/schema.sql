create table users
(
	id		int unsigned not null auto_increment primary key,
	first		varchar(64) not null,
	last		varchar(64) not null,
	date		datetime,
	username	varchar(32) not null,
	password	varchar(32) not null,
	email		varchar(128),
	superuser	enum('t', 'f') not null default 'f',
	cash		float default 0.0,
	worth		float default 0.0,
	worthDate	datetime
);

create table symbols
(
	id		int unsigned not null auto_increment primary key,
	symbol		varchar(32) not null,
	price		float,
	pointChange	float,
	company		varchar(64),
	time		datetime
);

create table orders
(
	id		int unsigned not null auto_increment primary key,
	time		datetime,
	userId		int not null,
	action		enum('buy', 'sell') not null,
	symbol		varchar(32) not null,
	quantity	float not null,
	price		float not null,
	transactionTime	datetime,
	status		enum('open', 'filled', 'denied', 'canceled') not null,
	comments	varchar(32)
);

create table portfolios
(
	userId		int not null,
	symbolId	int not null,
	quantity	float not null
);

create table groups
(
	id		int unsigned not null auto_increment primary key,
	name		varchar(32),
	ordinal		int not null
);

create table usersGroups
(
	userId		int not null,
	groupId		int not null
);

create table splits
(
	symbolId	int not null,
	symbol		varchar(32) not null,
	date		datetime,
	post		int not null,
	previous	int not null
);

-- select o.id, o.time, concat( u.first, " ", u.last ) as name,
--        o.action, o.symbol, o.quantity, o.price 
-- from orders o, users u 
-- where status = 'open' and o.userId = u.id;

-- select s.id 
-- from symbols s 
-- left join portfolios p 
-- on s.id = p.symbolId 
-- where p.symbolId is null;
