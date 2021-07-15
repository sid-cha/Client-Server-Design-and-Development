
package com.chauhan.siddharth.business;
import com.chauhan.siddharth.service.*;
import companydata.*;
import java.util.*;

public class BusinessLayer {
    DataLayer dl = null;


    public Boolean deptNumber(String departmentNumber, List<String> departmentNumbers) {
        if (departmentNumbers.contains(departmentNumber)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean company(String company) {
        if (company.equals("sc2816")) {
            return true;
        } else {
            return false;
        }
    }
    public Boolean deptNo(Integer departmentId, String departmentNumber, Department dept) {
        if (dept.getDeptNo().equals(departmentNumber) && dept.getId() != departmentId) {
            return true;
        } else {
            return false;
        }
    }
    public Boolean deptID(Integer departmentId, List<Integer> departmentIDList) {
        if (departmentIDList.contains(departmentId)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean manager(Integer mng_id, List<Integer> EmployeeIds) {
        if (mng_id != 0 && !EmployeeIds.contains(mng_id)) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean employeeNumber(String employeeNumber, List<String> employeeNumberList) {
        if (employeeNumberList.contains(employeeNumber)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean employeeNumbers(Integer employeeID, String employeeNumber, Employee employee) {
        if (employee.getEmpNo().equals(employeeNumber) && employee.getId() != employeeID) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean employeeIds(Integer employeeID, List<Integer> empIdList) {
        if (empIdList.contains(employeeID)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean hireDate(java.util.Date hireDate, java.util.Date today) {
        if (today.compareTo(hireDate) < 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean weekend(String day) {
        if (day.equals("Saturday") || day.equals("Sunday")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean startTime(java.util.Date start, java.util.Date now) {
        if (now.compareTo(start) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean hours(java.util.Calendar start, java.util.Calendar end) {
        if (start.HOUR_OF_DAY < 8 || start.HOUR_OF_DAY > 18 || end.HOUR_OF_DAY < 9 || end.HOUR_OF_DAY > 19) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean dayCheck(java.util.Date start, java.util.Date end) {
        long dateDifference = (end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000);
        if (dateDifference != 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean hourCheck(java.util.Date start, java.util.Date end) {
        long timeDiff = (end.getTime() - start.getTime()) / 60000;
        if (timeDiff <= 60) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean newDay(java.util.Date timecardDate, java.util.Date startDate,

            Integer timecardEmpId, Integer employeeId) {
        long dayDifference = (timecardDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
        if (employeeId == timecardEmpId && dayDifference == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean timeCards(Integer id, List<Integer> idLists) {
        if (!idLists.contains(id)) {
            return true;
        } else {
            return false;
        }
    }
}