# Childcare-Checkin Java Project
Author: Timothy Chung, Zhen Xu, Zhou Zhangyangzi Betty

The heavy non-teaching workload is a common pain point for pre-k teachers. Among all the administrative duties, managing children's attendance and performance records is one of the most time-consuming whereas critical work in the daily operation. A secure, efficient and automated application can save a significant amount of manual work, improve parent-teacher communication, and facilitate the performance tracking. 
Our application (Childcare check-in system ) is intended for pre-k student management with respect to attendance, check-out, and note documentation. The application allows daycare teachers to load class information data, manage daily check-in/out, rate individual performance and pull up individual performance reports as needed. 

It has main 4 functions:
1) Register and manage student info
2) Check in management
3) Check out management
4) Create a performance report
We connect to aws to manage the data. 

Details about the project proposal: https://drive.google.com/file/d/1dzvpwXyQf3NhnSFWXXPC_fF6hyvCSaeh/view

## Instruction on setting up the environment
Step 1: 
Download and Install IntelliJ IDEA
For windows:
https://www.jetbrains.com/idea/download/#section=windows
For mac:
https://www.jetbrains.com/idea/download/#section=mac

Step 2： 
Clone the repository from GitHub 
https://github.com/UPenn-CIT599/final-project-team13_childcare_checkin

Step 3:
Import the project to IntelliJ IDEA and follow the instructions to complete the importing 
![Test Image 1](import_project.png)

Step 4:
Add dependency to the project. 
Right click the project name, go to the “Open Module Settings”
![Test Image 1](import_dependency_1.png)
Go to Modules - Click “+” - “JARs or directories”
![Test Image 1](import_dependency_2.png)
Import the itextpdf-5.5.13.1.jar and mysql-connector-java-8.0.18.jar
![Test Image 1](import_dependency_3.png)
Click Apply - Ok

Step 5:
Compile the src/MainFunction
Missing “JUnit” package error will show up, follow the instruction and add the package.
![Test Image 1](import_junit_test.png)

Step 6:
After all the installation above, you may proceed with all testing cases under test/GUI

The test cases are static and some of them are dependent on the number of records in the database. Please run the tests first because some tests may fail after you run the project and insert more record in the database. 
![Test Image 1](run_test.png)

Step 7:
After the tests are run, you may start to test the UI.
![Test Image 1](ui_sample.png)


## Design CRC Cards
Registration Class

| Respnsibilities | Collaborators |
| --------------  | ------------- |
| Have: <br/> * Student ID <br/> * First Name <br/> * Last Name <br/> * DOB <br/> * Gender <br/> * Phone <br/> * Parent First Name <br/> * Parent Last Name <br/> * Enrolled Class <br/> * Attendance Status <br/> <br/> Set Student info <br/> Get Student info <br/> Enroll in class <br/> change class <br/> drop class | Class <br/> Attendance|

Check-in Class 

| Respnsibilities | Collaborators |
| --------------  | ------------- |
| Have: <br/> * Attendance ID <br/> * Date <br/> * Student ID <br/> * Attendance Type <br/> * Drop-off Parent <br/> * Temperature <br/> * Check-in Time <br/> * Absent reason <br/><br/> Set attendance info <br/> Get attendance info | |

Check-out Class 

| Respnsibilities | Collaborators |
| --------------  | ------------- |
| Have: <br/> * Attendance ID <br/> * Date <br/> * Student ID <br/> * Pick-up Parent <br/> * Check-out Time <br/> * Class performance rating <br/><br/> Set attendance info <br/> Get attendance info | |

Report Class 

| Respnsibilities | Collaborators |
| --------------  | ------------- |
| Have: <br/> * Date <br/>  * Student ID <br/>  * Class ID <br/> * Attendance Type <br/> * Temperature <br/> * Performance rating <br/> * Absent Reason <br/> <br/> Get check-in <br/>  Get check-out <br/> Create report on different type/time basis <br/> Save to local files | Check-in Class <br/> Check-out Class|


## SQL Database structure documentation
Student table 

|       Columns   | Data Type |
| --------------- | --------- |
|     `student_id`  | Int (PK)  |
|     `last_name`   | Varchar   |
|     `first_name`  | Varchar   |
|     `birth_date`  | Date      |
|       `gender`    | Varchar   |
|  `phone_number`  | Varchar   |
|`parent_first_name`| Varchar   |
|`parent_last_name` | Varchar   |
|`enrolled_class_id`| Varchar   |
|`attendence_status`| Varchar   | 

Class table 

| Columns           |  Data Type |
| ------------------ | ---------- |
|      `class_id`     | Varchar(PK)|
|        `name`      |   Varchar  |
| `teacher_last_name` |   Varchar  |
| `teacher_first_name`|   Varchar  |
|    `teacher_id`     | Varchar(FK)|
|      `semester`     |    Varchar | 

Attendance table 

| Columns           |  Data Type |
| --- | --- |
|   `attendence_id`    |  Int (PK)  |
|      `student_id`    |   Int (FK) |
|      `class_id`      |Varchar(FK) |
|  `attendance_type`   |   Varchar  |
|`drop_off_parent_name` |   Varchar  |
|    `temperature`     |    float   | 
|   `absent_reason`    |   Varchar  |
|  `check_in_dt_tm`    | timestamp  | 
|  `check_out_dt_tm`   | timestamp  | 
| `pick_up_parent_name` |   Varchar  |
|`class_performance_rt` |     Int    |

We will add teacher table in the database later if needed. 

## Demo