var moment = require("moment");
var DataLayer = require("./companydata/index.js");
var company = "sc2816";
var dl = new DataLayer(company);
module.exports = {
    Company: function(company) 
    {
        if(company == "sc2816")
        {
            return true;
        } else {
            return false;
        }
    },
    DepartmentIdExist: function(department_ID)
    {
        var departmentID_List = [];
        var allDepartments = dl.getAllDepartment(company);
        for(let d of allDepartments)
        {
            departmentID_List.push(d.getId());
        }
        if(!departmentID_List.includes(department_ID))
        {
            return false;
        } else 
        {
            return true;
        }
    },
    UniqueDepartmentNumber: function(department_Number,department_ID)
    {
        var allDepartments = dl.getAllDepartment(company);
        for(let d of allDepartments)
        {
            if(d.getDeptNo() == department_Number && d.getId() != department_ID){
            department_Number = department_Number.concat("_1");
            }
        }
        return department_Number 
    },
    UniqueDepartment: function(department_Number)
    {
        var departmentList = [];
        var allDepartments = dl.getAllDepartment(company);
        for(let d of allDepartments)
        {
        departmentList.push(d.getDeptNo());  
        }
        if(departmentList.includes(department_Number))
        {
            return false;
        }
        return true; 
    },
    
    
    validateUniqueEmployeeNumber: function(employee_Number) 
     {
        var allEmployeeNumberList = [];
        var allEmployee = dl.getAllEmployee(company);
        for(let d of allEmployee)
        {
            allEmployeeNumberList.push(d.getEmpNo());  
        }
        if(allEmployeeNumberList.includes(employee_Number))
        {
            employee_Number = employee_Number.concat("_1");
        }
        return employee_Number 
    },
    validateUniqueEmpNo_EmpId: function(employee_Number,employeeID)
    {
        var allEmployee = dl.getAllEmployee(company);
        for(let employee of allEmployee)
        {
            if(employee.getEmpNo()==employee_Number && employee.getId() != employeeID)
            {
                employee_Number=employee_Number.concat("_1");
            }
        }
        return employee_Number 
    },
    validateEmployeeIdExist: function(employee_ID)
    {
        var employeeList = [];
        var allEmployee = dl.getAllEmployee(company);
        for(let employee of allEmployee)
        {
            employeeList.push(employee.getId());
        }
        if(!employeeList.includes(employee_ID))
        {
            return false;
        } else 
        {
            return true;
        }
    },
    validateDate: function(date) 
    {
        var validating_Date = moment(date)
        var now = moment();
        if(validating_Date.isSameOrBefore(now))
        {
            return true;
        } else 
        {
            return false;
        }

    },
    validateDay_Week: function(date) 
    {
        var now = moment(date);
        if(now.day() != 0 && now.day() != 6 )
        {
            return true;
        } else 
        {
            return false;
        }
    },
    validateTimeId: function(timeCardID)
    {
        var cardID = dl.getTimecard(timeCardID);
        if(cardID == null)
        {
            return false;
        } else
        {
            return true;
        }
    },
    validateDay_Range: function(date)
    {
        var validating_Date = moment(date)
        var now = moment()
        var rangeDay = now.subtract(7,'days')
        if(validating_Date.isSameOrAfter(rangeDay))
        {
            return true;
        } else
        {
            return false;
        }
    },
    validateTime_Range: function(time) 
    {
        var validating_Date = moment(time);
        if(validating_Date.hour()>=6 && validating_Date.hour()<17)
        {
            return true;
        } else if(validating_Date.minute()==0 && validating_Date.hour() ==17 )
        {
            return true;
        }else 
        {
            return false;
        }
    },
    validateTime_End: function(time)
    {
        var validating_Date = moment(time);
        if(validating_Date.hour()<18 && validating_Date.hour()>=7 )
        {
            return true;
        } else if(validating_Date.minute()==0 && validating_Date.hour() ==18)
        {
            return true;
        }else 
        {
            return false;
        }
    },
    validateTime_difference: function(time_one, time_two)
    {
        var validation_time_one = moment(time_one);
        var validation_time_two = moment(time_two);
        if(validation_time_one.isBefore(validation_time_two,'hour'))
        {
            return true;
        } else 
        {
            return false;
        }
    },
    validate_Employee_Time_Difference: function(employeeID, time) 
    {
        var validating_Date = moment(time);
        var allTimeCards = dl.getAllTimecard(employeeID);
        for(let timeCards of allTimeCards)
        {
            var d = moment(timeCards.getStartTime());
            if(timeCards.getEmpId() == employeeID && validating_Date.isSame(d,'day'))
            {
                return false;
            } 
        }
        return true;
    },
    validate_Employee_Time_Timecard: function(employeeID, time, timecard_id)
    {
        var validating_Date = moment(time);
        var allTimeCards = dl.getAllTimecard(employeeID);
        for(let timeCards of allTimeCards)
        {
            var d = moment(timeCards.getStartTime());
            if(timeCards.getEmpId() == employeeID && validating_Date.isSame(d,'day') && timeCards.getId() != timecard_id)
            {
                return false;
            } else 
            {
                return true;
            }
        }
    },
    validate_date_difference: function(time_one, time_two)
    {
        var validation_time_one = moment(time_one);
        var validation_time_two = moment(time_two);
        if(validation_time_one.isSame(validation_time_two,'day')){
            return true;
        } else {
            return false;
        }
    }
}