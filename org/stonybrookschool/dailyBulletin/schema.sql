Select for Bulletin
-- select p.priority, p.head, p.publishdate, p.text, u.name 
-- from posts p, users u 
-- where p.state='published'
--   and p.publishDate < now( )
--   and p.unpublishedDate > now( )
--   and p.userId=u.Id
-- order by p.priority asc, p.publishDate desc

Select for Edit
-- select p.id, p.date, p.publishDate, p.unpublishDate, p.head, p.text, 
--	  p.userId, p.state, p.student, p.staff, p.parent, p.priority, 
--	  u.name, u.staff
-- from posts p, users u
-- where ( p.state='pending' and p.userId=u.Id )
--   or  ( p.state='published' and p.publishDate < now( ) and p.userId=u.Id )
-- order by publishDate

Select for Admin
-- select p.id, p.date, p.publishDate, p.unpublishDate, p.head, p.text,
--	  p.userId, p.state, p.student, p.staff, p.parent, p.priority,
--	  u.name
-- from posts p, users u
-- where p.unpublishDate > now ( ) 
-- order by p.state desc, p.priority, publishDate desc

create table stories
(
	id		int unsigned not null auto_increment primary key,
	date		datetime not null,
	publishDate	datetime not null,
	unpublishDate	datetime not null,
	head		varchar( 64 ),
	text		text not null,
	userId		int not null,
	state		enum( 'pending', 'published', 'unpublished', 'deleted' ),
	student		enum( 't', 'f' ),
	staff		enum( 't', 'f' ),
	parent		enum( 't', 'f' ),
	priority	int
);

create table users
(
	id		int not null auto_increment Primary key,
	username	varchar( 64 ) not null,
	password	varchar( 32 ) not null,
	email		varchar( 64 ),
	first		varchar( 32 ),
	last		varchar( 32 ),
	publisher	enum( 't', 'f' ),
	staff		enum( 't', 'f' )
);

create table priorities
(
        id              int not null auto_increment primary key,
	name		varchar( 64 )
);

insert into priorities values( null, 'highest' );
insert into priorities values( null, 'higher 8' );
insert into priorities values( null, 'higher 7' );
insert into priorities values( null, 'higher 6' );
insert into priorities values( null, 'higher 5' );
insert into priorities values( null, 'higher 4' );
insert into priorities values( null, 'higher 3' );
insert into priorities values( null, 'higher 2' );
insert into priorities values( null, 'higher 1' );
insert into priorities values( null, 'normal' );
insert into priorities values( null, 'lower 1' );
insert into priorities values( null, 'lower 2' );
insert into priorities values( null, 'lower 3' );
insert into priorities values( null, 'lower 4' );
insert into priorities values( null, 'lower 5' );
insert into priorities values( null, 'lower 6' );
insert into priorities values( null, 'lower 7' );
insert into priorities values( null, 'lower 8' );
insert into priorities values( null, 'lowest' );

-- should change stories.priority to stories.priorityId
-- kill stories.student, stories.staff and stories.parent

create table editions
(
	id		int not null auto_increment primary key,
	name		varchar( 64 ) not null,
	diaplayName	varchar( 128 ),
	url		varchar( 128 )
);

insert into editions values( null, 'student', 'Student Edition', 'http://bulletin.stonybrookschool.org/' );
insert into editions values( null, 'staff', 'Staff Edition', 'http://bulletin.stonybrookschool.org/' );
insert into editions values( null, 'parent', 'Parent Edition', 'http://bulletin.stonybrookschool.org/' );

create table storiesToEditions
(
	storyId		int not null,
	editionId	int not null
);

-- new additions for the poll system

create table polls
(
	id		int not null auto_increment primary key,
	date		datetime not null,
	publishDate	datetime not null,
	unpublishDate	datetime not null,
	question	varchar( 255 ),
	userId		int not null,
	state		enum( 'pending', 'published', 'unpublished', 'deleted' ),
	priorityId	int not null,
	resultsPublic	enum( 't', 'f' ) default 't'
);

create table pollOptions
(
	id		int not null auto_increment primary key,
	pollId		int not null,
	answer		varchar( 64 ),
	ordinal		int not null,
	votes		int not null default 0
);

create table votes
(
	userId		int not null,
	pollId		int not null,
	pollOptionId	int not null,
	date		datetime
);

create table pollsToEditions
(
	storyId		int not null,
	pollId		int not null
);

-- show poll results:
--
-- select concat( u.first, ' ', u.last ) as name, po.answer, v.date from votes v, users u, pollOptions po where v.userId = u.id and pollOptionId = po.id and v.pollId = 1 order by v.date;
