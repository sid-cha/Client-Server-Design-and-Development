var express = require("express");
var moment = require("moment");
var validation = require("./business.js")
var app = express();

app.use(express.json());
var urlencodedParser = app.use(express.urlencoded({ extended: false }));

var DataLayer = require("./companydata/index.js");
var dl = new DataLayer("sc2816");
var response = "";

//1. DELETE Company
app.delete('/CompanyServices/company', function(req, res, next) 
{
    try
     {
        var company = req.query.company;
        dl = new DataLayer(company);
        var val = dl.deleteCompany(company);
        if (val <= 0)
        {
            response = {error:"Company does not exist. Please try again"};
        } else 
        {
            response = {success:company+"'s information deleted."};
        }
     } catch (exp) 
     {
        response = {error:"Company cannot be deleted"};
     }  
    res.send(JSON.stringify(response));
}); //DELETE

//2. GET department using commpany and dept_id
app.get('/CompanyServices/department', function(req, res, next) 
{
    try
    {
        var company = req.query.company;
        var dept_id = parseInt(req.query.dept_id);
        dl = new DataLayer(company);
        if(validation.DepartmentIdExist(dept_id) == true)
        {
            var data = dl.getDepartment(company,dept_id);  
            response = {
                    dept_id:data.getId(),
                    company:data.getCompany(),
                    dept_name:data.getDeptName(),
                    dept_no:data.getDeptNo(),
                    location:data.getLocation()
            }
        } else 
        {
            response = {error: "Department id does not exist"}
        }
    } catch (exp)
    {
        response = {error: "unable to fetch the depatment id: "+dept_id+"."};
    } 
    res.send(JSON.stringify(response));
}); //GET

//3. GET all departments for a company
app.get('/CompanyServices/departments', function(req, res, next) 
{
    try
    {
        var company = req.query.company;
        dl = new DataLayer(company);
        var allDepartments = dl.getAllDepartment(company);
        var department_list = [];
        for(let data of allDepartments )
        {    	  
            var dept = {
                dept_id:data.getId(),
                company:data.getCompany(),
                dept_name:data.getDeptName(),
                dept_no:data.getDeptNo(),
                location:data.getLocation()
            }
            department_list.push(dept)
        }
        response = department_list;
    } catch (exp)
    {
        response = {error: "No Department found for existing company."};
    }  
    res.send(JSON.stringify(response));
});//GET

//4 PUT(update) department */
app.put('/CompanyServices/department', function(req, res, next) 
{
    try
    {
        var company = req.body.company;
        var dept_id = parseInt(req.body.dept_id);       
        var dept_name = req.body.dept_name;
        var dept_no = validation.UniqueDepartmentNumber(req.body.dept_no,dept_id);
        var location = req.body.location;
        dl = new DataLayer(company);
        if(validation.DepartmentIdExist(dept_id) == true)
        {
            var department = dl.getDepartment(company,dept_id);         
            department.setDeptName(dept_name);
            department.setDeptNo(dept_no);
            department.setLocation(location);
   	        var data = dl.updateDepartment(department);
            response =
            {
                success:
                {
                    dept_id:data.getId(),
                    company:data.getCompany(),
                    dept_name:data.getDeptName(),
                    dept_no:data.getDeptNo(),
                    location:data.getLocation()
                }
            };
        } else 
        {
            response = {error: "Department with this department id was not found"}
        }
    } catch (exp)
    {
        response = {error:"Department cannot be updated"};
    } 
    res.send(JSON.stringify(response));
}); //PUT

//5. POST department */
app.post('/CompanyServices/department', function(req, res, next) 
{
    try
    {
        var company = req.body.company;
        dl = new DataLayer(company);
        var dept_name = req.body.dept_name;
        var dept_no = req.body.dept_no;
        var location = req.body.location;
        if(!validation.UniqueDepartment(dept_no))
        {
            response={error:"Department number is not unique"};
            return res.send(response);
        }
        
        var data = new dl.Department(company,dept_name,dept_no,location);
        data = dl.insertDepartment(data);
        if (data.getId()>0){
            response = 
            {
                success:
                {
                dept_id:data.getId(),
                company:data.getCompany(),
                dept_name:data.getDeptName(),
                dept_no:data.getDeptNo(),
                location:data.getLocation()
                }
            };
        } else 
        {
            response = {error:"Unable to insert new department"};
        }
        
    } catch (exp)
    {
        response = {error:"Department cannot be inserted"};
    }  
    res.send(JSON.stringify(response));
}); //POST

//6. DELETE department using company and dept_id
app.delete('/CompanyServices/department', function(req, res, next) 
{
    try 
    {
        var company = req.query.company;
        var dept_id = req.query.dept_id;
        dl = new DataLayer(company);
        var val = dl.deleteDepartment(company,dept_id);
        if (val <= 0)
        {
            response = {error:"No Department Found in database."};
        } else 
        {
            response = {success:"Department "+dept_id+" from "+company+" is deleted"};
        }
     } catch (exp) 
     {
        response = {error:"Department cannot be deleted"};
     }  
    res.send(JSON.stringify(response));
}); //DELETE



//7. GET employee using company and employee id
app.get('/CompanyServices/employee', function(req, res, next) 
{
    try
    {
      var company = req.query.company;
       var emp_id = parseInt(req.query.emp_id);
       dl = new DataLayer(company);
       if(validation.validateEmployeeIdExist(emp_id) == true)
       {
           var employee = dl.getEmployee(emp_id); 
           response = {
               emp_id:employee.getId(),
               emp_name:employee.getEmpName(),
               emp_no:employee.getEmpNo(),
               hire_date:employee.getHireDate(),
               job:employee.getJob(),
               salary:employee.getSalary(),
               dept_id:employee.getDeptId(),
               mng_id:employee.getMngId()
           };
       } else 
       {
           response = {error:"Employee Id does not exist."}
       }
   } catch (exp)
   {
       response = {error: "No employee found with this employee id: "+emp_id+"."};
   } 
   res.send(JSON.stringify(response));
});//GET

//8.  GET all the employees for a company
app.get('/CompanyServices/employees', function(req, res, next) 
{
    try
    {
       var company = req.query.company;
        dl = new DataLayer(company);
        var allEmployee = dl.getAllEmployee(company);
        var employee_list = [];
        for(let employee of allEmployee )
        {    	  
            var employee_group = {
                emp_id:employee.getId(),
                emp_name:employee.getEmpName(),
                emp_no:employee.getEmpNo(),
                hire_date:employee.getHireDate(),
                job:employee.getJob(),
                salary:employee.getSalary(),
                dept_id:employee.getDeptId(),
                mng_id:employee.getMngId()
            };
            employee_list.push(employee_group);
        }
        response = employee_list;
    } catch (exp)
    {
        response = {error: "Employees not found for this particular company"};
    } 
    res.send(response);
});//GET

//9. POST employee
app.post('/CompanyServices/employee', function(req, res, next) 
{
    try{
        var company = req.body.company;
        var emp_name = req.body.emp_name;
        var emp_no = validation.validateUniqueEmployeeNumber(req.body.emp_no);
        var hire_date = req.body.hire_date;
        var job = req.body.job;
        var salary = parseFloat(req.body.salary);
        var dept_id = parseInt(req.body.dept_id);
        var mng_id = parseInt(req.body.mng_id);
        if(!validation.validateEmployeeIdExist(mng_id))
        {
            mng_id = 0;
        }
    
        if(!validation.validateDate(hire_date))
        {
            response = {error:"hire_date must be a valid date equal to the current date or earlier."};
        }
        else if(!validation.validateDay_Week(hire_date))
        {
            response = {error:"hire_date must be a Monday, Tuesday, Wednesday, Thursday or a Friday. It cannot be Saturday or Sunday."};
        }
        else if(validation.Company(company) && validation.DepartmentIdExist(dept_id))
        {
            dl = new DataLayer(company);
            
            var employee = dl.insertEmployee(new dl.Employee(emp_name,emp_no,hire_date,job,salary,dept_id,mng_id));
            response = 
            {
                success:
                {
                    emp_id:employee.getId(),
                    emp_name:employee.getEmpName(),
                    emp_no:employee.getEmpNo(),
                    hire_date:employee.getHireDate(),
                    job:employee.getJob(),
                    salary:employee.getSalary(),
                    dept_id:employee.getDeptId(),
                    mng_id:employee.getMngId()
                }
            };
        
        } else 
        {
            response = {error:"No department Id found."};
        }
        
    } catch (exp)
    {
        response = {error:"Unable to insert new employee."};
    }  
    res.send(JSON.stringify(response));
});//POST

//10 PUT employee 
app.put('/CompanyServices/employee', function(req, res, next) 
{
    try
    {
        var company = req.body.company;
        var emp_name = req.body.emp_name;
        var emp_id = parseInt(req.body.emp_id);
        var emp_no = validation.validateUniqueEmpNo_EmpId(req.body.emp_no,emp_id);
        var hire_date = req.body.hire_date;
        var job = req.body.job;
        var salary = parseFloat(req.body.salary);
        var dept_id = parseInt(req.body.dept_id);
        var mng_id = parseInt(req.body.mng_id);
        if(!validation.validateEmployeeIdExist(mng_id))
        {
            mng_id = 0;
        } 
        if(!validation.validateEmployeeIdExist(emp_id))
        {
            response = {error:"No Employee ID exist"};
        } 
        else if(!validation.validateDay_Week(hire_date))
        {
            response = {error:"hire_date must be a hire_date must be a Monday, Tuesday, Wednesday, Thursday or a Friday. It cannot be Saturday or Sunday."};
        }
        else if(!validation.validateDate(hire_date))
        {
            response = {error:"hire_date must be a valid date"};
        }
      
        else if(validation.Company(company) && validation.DepartmentIdExist(dept_id))
        {
            dl = new DataLayer(company);
            var employee = dl.getEmployee(emp_id);
            employee.setEmpName(emp_name);
            employee.setEmpNo(emp_no);
            employee.setHireDate(hire_date);
            employee.setJob(job);
            employee.setSalary(salary);
            employee.setDeptId(dept_id);
            employee.setMngId(mng_id);
            employee = dl.updateEmployee(employee);
            response = 
            {
                success:
                {
                    emp_id:employee.getId(),
                    emp_name:employee.getEmpName(),
                    emp_no:employee.getEmpNo(),
                    hire_date:employee.getHireDate(),
                    job:employee.getJob(),
                    salary:employee.getSalary(),
                    dept_id:employee.getDeptId(),
                    mng_id:employee.getMngId()
                }
            };
        } else 
        {
            response = {error: "Department Id does not exist"}
        }
    } catch (exp)
    {
        response = {error:"Current Employee cannot be updaated"};
    }  
    res.send(JSON.stringify(response));
});//PUT

//11 DELETE employee 
app.delete('/CompanyServices/employee', function(req, res, next) 
{
    try 
    {
        var company = req.query.company;
         var emp_id = req.query.emp_id;
        dl = new DataLayer(company);
        var val = dl.deleteEmployee(emp_id);
        if (val <= 0)
        {
            response = {error:"Employee ID not found"};
        } else 
        {
            response = {success:"Employee "+emp_id+" is deleted."};
        }
    } catch (exp) 
    {
        response = {error:"Employee cannot be deleted"};
    }   
    res.send(JSON.stringify(response));
});//DELETE

//12 GET timecard for company
app.get('/CompanyServices/timecard', function(req, res, next) 
{
    try
    {
        var company = req.query.company;
         var timecard_id = parseInt(req.query.timecard_id);
        dl = new DataLayer(company);
        if(validation.validateTimeId(timecard_id)){
            var timeCard = dl.getTimecard(timecard_id); 
            response = {
                timecard_id:timeCard.getId(),
                start_time:timeCard.getStartTime(),
                end_time:timeCard.getEndTime(),
                emp_id:timeCard.getEmpId()
            };
        } else 
        {
            response = {error:"Time card not found"}
        }
    } catch (exp)
    {
        response = {error: "Cannot get timecard with timecard_id: "+timecard_id+"."};
    } 
    res.send(JSON.stringify(response));
});//GET

//13 GET timecards for a company using employee id 
app.get('/CompanyServices/timecards', function(req, res, next) 
{
    try
    {
        var company = req.query.company;
        var emp_id = req.query.emp_id;
        dl = new DataLayer(company);
        if(!validation.validateEmployeeIdExist(emp_id))
        {
            var allTimeCards = dl.getAllTimecard(emp_id);
            var timeCardsList = [];
            for(let timeCard of allTimeCards )
            {    	  
                var timecard = {
                timecard_id:timeCard.getId(),
                start_time:timeCard.getStartTime(),
                end_time:timeCard.getEndTime(),
                emp_id:timeCard.getEmpId()
                };
                timeCardsList.push(timecard);
            }
            response = timeCardsList;
        } else 
        {
            response = {error: "Time card not found for this employee id"};
        }
    } catch (exp)
    {
        response = {error: "Cannot get this timecards."};
    }  
    res.send(response);
}); // GET

//14 POST timecard  editing left
app.post('/CompanyServices/timecard', function(req, res, next) 
{
    try
    {
        var company = req.body.company;
        var start_time = req.body.start_time;
        var end_time = req.body.end_time;
        var emp_id = parseInt(req.body.emp_id);

        if(!validation.validateDate(start_time))
        {
            response = {error:"start_time must be a valid date equal to the current date or earlier."};
        }
        else if(!validation.validateDay_Range(start_time))
        {
            response = {error:"start_time must be a valid date not later than a week of today."};
        }
        else if(!validation.validateDay_Week(start_time))
        {
            response = {error:"start_time must be valid must be a weekday."};
        }
        else if(!validation.validate_date_difference(start_time,end_time))
        {
            response = {error:"end_time must be valid must be on the same day as the start date."};
        } 
        else if(!validation.validate_Employee_Time_Difference(emp_id,start_time))
        {
            response = {error:"start_time must be valid must not be the same day as other timecard"};
        }
        else if(!validation.validateTime_Range(start_time))
        {
            response = {error:"start_time must be valid should not be no earlier than 6:00 and cannot later than 17:00"};
        }
        else if(!validation.validateTime_End(end_time))
        {
            response = {error:"end_time must be valid should not be no earlier than 7:00 and cannot later than 18:00"};
        }
        else if(!validation.validateTime_difference(start_time,end_time))
        {
            response = {error:"end_time must be valid must be at least 1 hour greater than the start_time"};
        }
     
        else if(validation.Company(company) && validation.validateEmployeeIdExist(emp_id))
        {
            dl = new DataLayer(company);
            var timeStamp = new dl.Timecard(start_time,end_time,emp_id);
            var timeCard = dl.insertTimecard(timeStamp);
            response = 
            {
                success:
                {
                    timecard_id:timeCard.getId(),
                    start_time:timeCard.getStartTime(),
                    end_time:timeCard.getEndTime(),
                    emp_id:timeCard.getEmpId()
                }
            };
        
        } else 
        {
            response = {error:"Employee ID does not exist."};
        }
        
    } catch (exp)
    {
        response = {error:"Cannot insert timecard."};
    }   
    res.send(JSON.stringify(response));
}); //POST

//15  PUT timecard editing left
app.put('/CompanyServices/timecard', function(req, res, next) 
{
    try
    {
        var company = req.body.company;
        var timecard_id = req.body.timecard_id;
        var start_time = req.body.start_time;
        var end_time = req.body.end_time;
        var emp_id = parseInt(req.body.emp_id);
        if(!validation.validateTimeId(timecard_id))
        {
            response = {error:"Time card ID NOT FOUND"};
        }
        
        else if(!validation.validateDate(start_time))
        {
            response = {error:"start_time must be a valid date equal to the current date or earlier."};

           
        }
        else if(!validation.validateDay_Range(start_time))
        {
            response = {error:"start_time must be a valid date not later than a week of today."};

            
        }
        else if(!validation.validateDay_Week(start_time))
        {
            response = {error:"start_time must be valid must be a weekday."};

          
        }
        else if(!validation.validateTime_Range(start_time))
        {
            response = {error:"start_time must be valid should not be no earlier than 6:00 and cannot later than 17:00"};

           
        }
        else if(!validation.validateTime_End(end_time))
        {
            response = {error:"end_time must be valid should not be no earlier than 7:00 and cannot later than 18:00"};

           
        }
        else if(!validation.validateTime_difference(start_time,end_time))
        {
            response = {error:"end_time must be valid must be at least 1 hour greater than the start_time"};

           
        }
        else if(!validation.validate_date_difference(start_time,end_time))
        {
            response = {error:"end_time must be valid must be on the same day as the start date."};

           
        } 
        else if(!validation.validate_Employee_Time_Timecard(emp_id,start_time,timecard_id))
        {
            response = {error:"Not valid start_time: must not be the same day as other timecard"};
        }
        else if(validation.Company(company) && validation.validateEmployeeIdExist(emp_id))
        {
            dl = new DataLayer(company);
            var time = dl.getTimecard(timecard_id);
            time.setEmpId(emp_id);
            time.setStartTime(start_time);
            time.setEndTime(end_time);
   	        var timeCard = dl.updateTimecard(time);   
            response = 
            {
                success:
                {
                    timecard_id:timeCard.getId(),
                    start_time:timeCard.getStartTime(),
                    end_time:timeCard.getEndTime(),
                    emp_id:timeCard.getEmpId()
                }
            };
        
        } else {
            response = {error:"This emp_id does not exist."};
        }
    } catch (exp){
        response = {error:"Cannot update this timecard."};
    }   
    res.send(JSON.stringify(response));
});// PUT

//16 DELETE timecard 
app.delete('/CompanyServices/timecard', function(req, res, next) 
{
    try 
    {
       var company = req.query.company;
        var timecard_id = req.query.timecard_id;
        dl = new DataLayer(company);
        var val = dl.deleteTimecard(timecard_id);
        if (val <= 0)
        {
            response = {error:"No timecard id found"};
        } else 
        {
            response = {success:"Timecard "+timecard_id+" is deleted."};
        }
    } catch (exp) 
    {
        response = {error:"cannot delete this timecard "};
    }   
    res.send(JSON.stringify(response));
}); //DELETE


var server = app.listen(8080,function()
{
    var host = server.address().address;
    var port = server.address().port;
    console.log("server listening...");
});
