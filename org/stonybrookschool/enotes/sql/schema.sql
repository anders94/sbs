--
-- main data tables
-- modified by Steve Sides and Thom Brownworth 13-Aug-03 11:30 am

-- note: attendance is in the sample web pages, but not handled in this schema

-- comment these out if you're starting from scratch.
-- be sure you don't have a bunch of live data in there before running this.
drop table users,comments,disciplines,quicknotes;
drop table groups,parentsToStudents,homes,sections,departments;
drop table events,marks,crimesAndPunishments,quicknoteTypes;
drop table studentsToSections,periods,properties;

create table users
(
	userId		int unsigned not null auto_increment primary key,
	obsolete	tinyint not null,
	timestamp	datetime,
	first		varchar(64) not null,
	last		varchar(64) not null,
	title		enum('Mr.', 'Mrs.', 'Ms.','Dr.','Rev.','Hon.','') default '',
	username	varchar(32) not null,
	password	varchar(32) not null,
	email		varchar(128),
	groupId		int not null,
	academicId	int not null,
	yearId		int not null,
	schoolId	int not null,
	counselorUserId	int,
	homeId		int,
	superuser	enum('t', 'f') not null default 'f'
);
-- do all users have permission to use the system? if you are going
-- to make a comment / discipline / quicknote on the guy, they need
-- to have an entry in this table and hence a login... not all
-- fields are usefull for every user and hence are not required.

create table comments
(
	commentId	int unsigned not null auto_increment primary key,
	obsolete	tinyint not null,
	timestamp	datetime not null,
	studentUserId	int not null,
	sectionId	int not null,
	eventId		int not null,
	markId		int not null default 0,
	commentText	text not null,
	status		enum('pending','published','proofed','deleted') not null
);
-- as you should only do one comment per user per section per "event"
-- we require an eventId. an event may be "2003 Semester 1 Comments" or
-- "2003 Semester 1 Deficiency Comments" but are always a scheduled occurance.
-- every student need not have a comment for each eventId. you can
-- derive the teacher's userId by looking up the sectionId. you mention spell
-- check in comments. there are several packages we can use for this. i
-- guess this would be an "after we get this whole thing built" thing.
-- i opted NOT to have comments individually published. just to have the
-- whole event batch published or not. this can easily be changed...

create table disciplines
(
	disciplineId	int unsigned not null auto_increment primary key,
	obsolete	tinyint not null,
	timestamp	datetime not null,
	studentUserId	int not null,
	sectionId	int not null,
	capId		int not null,  /* index into table crimeAndPunishment */
	commentText	text,
	status		enum('pending','published','deleted') not null
);
-- you can look up the faculty member that created this discipline note through
-- the sectionId. can disciplines be set outside the context of a section?
-- like can a teacher set a discipline note for a student that is not his
-- own for walking on the grass or somesuch?
-- i added a freeform comment field to these as well.

create table quicknotes
(
	quicknoteId	int unsigned not null auto_increment primary key,
	obsolete	tinyint not null,
	timestamp	datetime not null,
	studentUserId	int not null,
	sectionId	int not null,
	quicknoteTypeId	int not null,
	commentText	text,
	published	tinyint not null,
	status		enum('pending','published','deleted') not null
);
-- can a quicknote be posted by a faculty outside the context of a section?
-- (same question as the discipline question) i also put a published flag
-- on these. the application may set them to published by default.











--
-- support data tables
--

create table groups
(
	groupId		int unsigned not null auto_increment primary key,
	groupName		varchar(32) not null,
	groupDescription	varchar(255)
);
-- example: "faculty", "parent", "12th Grade Student", etc.

create table parentsToStudents
(
	parentUserId	int not null,
	studentUserId	int not null
);
-- allows us to look up students "owned" by this parent or guardian
-- should we include a "relashionshipTypeId"? could establish
-- mother / father / guardian... probably not necessary...

create table crimesAndPunishments
(
	capId		int auto_increment primary key,
	offenseName	varchar(255) not null,
	punishmentName	varchar(255) not null,
	obsolete	tinyint not null
);

create table homes
(
	homeId		int unsigned not null auto_increment primary key,
	homeName	varchar(32) not null
);
-- example: "Monro", "Day Student"

create table sections
(
	id		int unsigned not null auto_increment primary key,
	name		varchar(32) not null, /*  section name */
	yearId		int not null,
	periodId	int not null,
	teacherUserId	int not null,
	sectionNumber	varchar(32),
	departmentId	int not null,
	honors		tinyint default 0
);
-- examples: "2003 Bible 12 Period B" these are unique sections that
-- include the year, period in which they meet and teacher. the goal
-- is not to ever change these so that 3 years later, a student can 
-- go back and see all his old classes even if the teacher has been 
-- replaced in subsiquent years. 

create table studentsToSections
(
	studentUserId	int not null,
	sectionId	int not null
);

create table departments
(
	departmentId	int unsigned not null auto_increment primary key,
	departmentName	varchar(32) not null
);
-- examples: "English", "Math"

create table marks
(
	markId		int unsigned not null auto_increment primary key,
	markName	varchar(32) not null
);
-- example: "A+", "B-", "F", "4", "3", "2.5", "Pass", "Fail" - every leagal grade
-- shouldn't need to be changed after it's initially set up unless the school
-- starts using another grading system.

create table events
(
	eventId		int unsigned not null auto_increment primary key,
	eventName	varchar(32) not null,
	eventYear	int not null,
	published	tinyint not null
);
-- example: "2003 Semester 1 Comments" "2003 Semester 1 Deficiency Comments". these are 
-- coordinated events or opportunities for faculty to create comments. when the
-- administrator is satisfied with all teacher comments for an event, they
-- set the "published" flag to true which allows students and parents to see 
-- comments tied to this event and disallows teachers to edit them.

create table quicknoteTypes
(
	quicknoteTypeId	int unsigned not null auto_increment primary key,
	quicknoteTypeName	varchar(32) not null
);
-- used in quicknotes. example: "Low Test Grade", "Dramatic Improvement"

create table periods
(
	periodId	int not null auto_increment primary key,
	periodName	varchar(32)
);

create table properties
(
	name	varchar(64),
	value	varchar(255)
);
