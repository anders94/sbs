create table users
(
	id		int unsigned not null auto_increment primary key,
	first		varchar(64) not null,
	last		varchar(64) not null,
	title		enum('Mr.', 'Mrs.', 'Ms.'),
	date		datetime,
	username	varchar(32) not null,
	password	varchar(32) not null,
	email		varchar(128),
	faculty		enum('t', 'f') not null default 'f',
	superuser	enum('t', 'f') not null default 'f',
	merits		float default 0.0,
	demerits	float default 0.0,
	yearId		int not null,
	studentCarPermission	enum('t', 'f') not null default 'f',
	adultCarPermission	enum('t', 'f') not null default 'f'
);

create table years
(
	id		int unsigned not null auto_increment primary key,
	year		int,
	class		varchar(32)
);

create table groups
(
	id		int unsigned not null auto_increment primary key,
	name		varchar(32) not null,
	description	varchar(255)
);

create table users_groups
(
	userId		int not null,
	groupId		int not null
);

create table events
(
	id		int unsigned not null auto_increment primary key,
	date		datetime not null,
	eventTypeId	int not null,
	purchases	int not null default 0
);

create table eventTypes
(
	id		int unsigned not null auto_increment primary key,
	name		varchar(32) not null,
	description	varchar(255),
	transportation	enum('t', 'f') not null,
	meal		enum('t', 'f') not null,
	lowPrice	float not null,
	highPrice	float,
	threshold	int not null
);

create table history
(
	date		datetime not null,
	giverUserId	int not null,
	recieverUserId	int not null,
	deltaMerits	float not null,
	deltaDemerits	float not null,
	eventId		int not null,
	reason		varchar(32)
);

create table passes
(
	id		int unsigned not null auto_increment primary key,
	date		datetime not null,
	userId		int not null,
	eventId		int not null,
	whereGoing	varchar(255),
	whenLeaving	datetime,
	whenReturning	datetime,
	transportationId    int not null,
	purchasePrice	float not null
);

create table transportations
(
	id		int unsigned not null auto_increment primary key,
	name		varchar(32) not null,
	description	varchar(255)
);

