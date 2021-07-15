package com.chauhan.siddharth.service;
import companydata.*;
import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.chauhan.siddharth.business.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.io.StringReader;


@Path("CompanyServices")
public class MyResource {

    @Context
    UriInfo uriInfo;
    DataLayer dl = null;
    BusinessLayer business = null;

    @Path("/company")
    @DELETE
    @Produces("application/json")
    public Response deleteCompany(@QueryParam("company") String company) {
        try {  dl = new DataLayer(company);
            int x = dl.deleteCompany(company);
            if (x == 0) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } 
            else {
                return Response.ok("{\"success\":\"" + company + "'s information deleted\"}\n").build();
            }
        } 
        catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }
    @Path("/department")
    @GET
    @Produces("application/json")
    public Response getDepartment(@QueryParam("company") String company, @QueryParam("dept_id") int dept_id) {
        try {
            dl = new DataLayer(company);
            Department dept = dl.getDepartment(company, dept_id);
            if (dept != null) {
                return Response.ok("{\n\"dept_id\":" + dept.getId() + ",\n" + 
                                    "\"company\":\"" + dept.getCompany() + "\",\n" + 
                                    "\"dept_name\":\"" + dept.getDeptName() + "\",\n"+ 
                                    "\"dept_no\":\"" + dept.getDeptNo() + "\",\n" + 
                                    "\"location\":\""+ dept.getLocation() + "\"\n}").build();
            } else {
                return Response.ok("{\"error\":\"Department does not exist.\"}\n").build();
            }
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/departments")
    @GET
    @Produces("application/json")
    public Response getAllDepartment(@QueryParam("company") String company) {
        try {
            dl = new DataLayer(company);
            List<Department> allDepartment = dl.getAllDepartment(company);
            List<String> dept_list = new ArrayList<String>();
            for (Department department : allDepartment) {
                dept_list.add("\n{\n\"dept_id\":" + department.getId() + ",\n" + 
                            "\"company\":\"" + department.getCompany() + "\",\n" + 
                            "\"dept_name\":\"" + department.getDeptName() + "\",\n"+ 
                            "\"dept_no\":\"" + department.getDeptNo() + "\",\n" +
                             "\"location\":\""+ department.getLocation() + "\"\n}");
            }
            return Response.ok(dept_list.toString()).build();
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/department")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateDepartment(String input) {
        try {
            JsonReader rdr = Json.createReader(new StringReader(input));
            JsonObject obj = rdr.readObject();
            int dept_id = obj.getInt("dept_id");
            String company = obj.getString("company");
            String dept_name = obj.getString("dept_name");
            String dept_no = obj.getString("dept_no");
            String location = obj.getString("location");
            dl = new DataLayer(company);            
            List<Department> allDepartments = dl.getAllDepartment(company);
            List<Integer> deptIdList = new ArrayList<Integer>();
            BusinessLayer business = new BusinessLayer();
            for (Department d : allDepartments) {
                deptIdList.add(d.getId());
            }
            if (!business.deptID(dept_id, deptIdList)) {
                return Response.ok("{\"error\":\"Department ID does not exist\"}\n").build();
            }
            for (Department d : allDepartments) {
                if (business.deptNo(dept_id, dept_no, d)) {
                    return Response.ok("{\"error\":\"Invalid Department Number.\"}\n").build(); 
                }
            }
            Department d = dl.getDepartment(company, dept_id);
            d.setDeptName(dept_name);
            d.setDeptNo(dept_no);
            d.setLocation(location);
            Department de = dl.updateDepartment(d);
            return Response.ok("{\n\"sucess\":{\n\"dept_id\":" + de.getId() + 
                                ",\n" + "\"company\":\""+ de.getCompany() + "\",\n" + 
                                "\"dept_name\":\"" + de.getDeptName() + "\",\n" +
                                 "\"dept_no\":\""+ de.getDeptNo() + "\",\n" + "\"location\":\"" + de.getLocation() + "\"\n}\n}").build();

        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/department")
    @POST
    @Produces("application/json")
    public Response insertDepartment(@FormParam("company") String company,
            @FormParam("dept_name") String dept_name, 
            @FormParam("dept_no") String dept_no,
            @FormParam("location") String location) {

        try {
            dl = new DataLayer(company);
            List<Department> allDepartment = dl.getAllDepartment(company);
            List<String> deptNumbers = new ArrayList<>();
            for (Department d : allDepartment) {
                deptNumbers.add(d.getDeptNo());
            }
            BusinessLayer business = new BusinessLayer();
            if (business.deptNumber(dept_no, deptNumbers)) {
                return Response.ok("{\"error\":\"No Department number found.\"}\n").build();
            }
            Department dept = new Department(company, dept_name, dept_no, location);
            dept = dl.insertDepartment(dept);
            if (dept.getId() > 0) {
                return Response.ok("{\n\"success\":{\n\"dept_id\":" + dept.getId() + ",\n" +
                                 "\"company\":\""+ dept.getCompany() + "\",\n" +
                                  "\"dept_name\":\"" + dept.getDeptName() + "\",\n" +
                                   "\"dept_no\":\"" + dept.getDeptNo() + "\",\n" +
                                    "\"location\":\"" + dept.getLocation() + "\"\n}\n}").build();

            } else {
                return Response.ok("{\"error\":\"Not able to insert.\"}\n").build();
            }
        } catch (Exception e)
         {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/department")
    @DELETE
    @Produces("application/json")
    public Response deleteDepartment(@QueryParam("company") String company,
            @QueryParam("dept_id") int dept_id) 
        {
        try {
            dl = new DataLayer(company);
            int deleted = dl.deleteDepartment(company, dept_id);
            if (deleted <= 0) {
                return Response.ok("{\"error\":\"Department ID does not exist\"}\n").build();
            } 
            else {
                return Response.ok("{\"success\":\"Department " + dept_id + " from" + company + " deleted\"}\n")
                        .build();
            }
        } catch (Exception e)
         {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }


    @Path("/employee")
    @GET
    @Produces("application/json")
    public Response getEmployee(@QueryParam("company") String company,
                                 @QueryParam("emp_id") int emp_id) {
        try {
            dl = new DataLayer(company);
            Employee employee = dl.getEmployee(emp_id);
            if (employee != null) {
                return Response.ok("{\n\"emp_id\":" + employee.getId() + ",\n" +
                         "\"emp_name\":\"" + employee.getEmpName()+ "\",\n" +
                          "\"emp_no\":\"" + employee.getEmpNo() + "\",\n" + 
                          "\"hire_date\":\""+ employee.getHireDate() + "\",\n" + 
                          "\"job\":\"" + employee.getJob() + "\",\n"+ 
                          "\"salary\":\"" + employee.getSalary() + "\",\n" + 
                          "\"dept_id\":\""+ employee.getDeptId() + "\",\n" + 
                          "\"mng_id\":\"" + employee.getMngId() + "\"\n}").build();
            } else {
                return Response.ok("{\"error\":\"Could not find any employee\"}\n").build();
            }
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/employees")
    @GET
    @Produces("application/json")
    public Response getAllEmployee(@QueryParam("company") String company) {
        try {
            dl = new DataLayer(company);
            List<Employee> allEmployeeList = dl.getAllEmployee(company);
            List<String> employeeList = new ArrayList<String>();
            for (Employee e : allEmployeeList) {
                employeeList.add("{\n\"emp_id\":" + e.getId() + ",\n" + 
                                "\"emp_name\":\"" + e.getEmpName()+ "\",\n" + 
                                "\"emp_no\":\"" + e.getEmpNo() + "\",\n" +
                                 "\"hire_date\":\"" + e.getHireDate() + "\",\n" +
                                  "\"job\":\"" + e.getJob() + "\",\n"+
                                   "\"salary\":\"" + e.getSalary() + "\",\n" +
                                    "\"dept_id\":\"" + e.getDeptId()+ "\",\n" + 
                                    "\"mng_id\":\"" + e.getMngId() + "\"\n}");
            }
            return Response.ok(employeeList.toString()).build();
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/employee")
    @POST
    @Produces("application/json")
    public Response insertEmployee(@FormParam("company") String company, 
            @FormParam("emp_name") String emp_name,
            @FormParam("emp_no") String emp_no, 
            @FormParam("hire_date") String hire_date,
            @FormParam("job") String job,
            @FormParam("salary") Double salary, 
            @FormParam("dept_id") int departmentId,
            @DefaultValue("0") @FormParam("mng_id") int mng_id) {
            business = new BusinessLayer();
        if (!business.company(company)) {
            return Response.ok("{\"error\":\"Wrong company name. Company name must be an valid RIT username\"}\n").build();
        }
        try {
           // business = new BusinessLayer();
            dl = new DataLayer(company);
            List<Department> allDepartments = dl.getAllDepartment(company);
            List<Integer> deptIDs = new ArrayList<Integer>();
            for (Department d : allDepartments) {
                deptIDs.add(d.getId());
            }
            if (business.deptID(departmentId, deptIDs)) {
                
                List<Employee> allemployees = dl.getAllEmployee(company);
                List<String> employeeNumber = new ArrayList<String>();
                List<Integer> employeeIDs = new ArrayList<Integer>();
                for (Employee employee : allemployees) {
                    employeeNumber.add(employee.getEmpNo());
                    employeeIDs.add(employee.getId());
                }
                if (!business.manager(mng_id, employeeIDs)) {
                    return Response.ok("{\"error\":\"Manager ID didn't matched.\"}\n").build();
                }
                if (business.employeeNumber(emp_no, employeeNumber)) {
                    return Response.ok("{\"error\":\"No EMployee found .\"}\n").build();
                }
                java.util.Date hireDate = new SimpleDateFormat("yyyy-MM-dd").parse(hire_date);
                java.util.Date date = java.sql.Date.valueOf(LocalDate.now());
                SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
                String dayOfWeek = simpleDateformat.format(hireDate);
                business = new BusinessLayer();
                if (business.hireDate(hireDate, date)) {
                    return Response.ok("{\"error\":\"Hire date must be valid, should be current date or earlier.\"}\n").build();
                }
                if (business.weekend(dayOfWeek)) {
                    return Response.ok("{\"error\":\"Hire date must be valid. Hire date cannot be Saturday or Sunday.\"}\n").build();
                }
                java.sql.Date hireDateIn = new java.sql.Date(hireDate.getTime());
                Employee employee = new Employee(emp_name, emp_no, hireDateIn, job, salary, departmentId, mng_id);
                employee = dl.insertEmployee(employee);
                return Response.ok("{\n\"success\":{\n\"emp_id\":" + employee.getId() + ",\n" + 
                                    "\"emp_name\":\"" + employee.getEmpName() + "\",\n" +
                                     "\"emp_no\":\"" + employee.getEmpNo() + "\",\n"+ 
                                     "\"hire_date\":\"" + employee.getHireDate() + "\",\n" +
                                      "\"job\":\"" + employee.getJob() + "\",\n" + 
                                      "\"salary\":\"" + employee.getSalary() + "\",\n" +
                                       "\"dept_id\":\"" + employee.getDeptId() + "\",\n" + 
                                       "\"mng_id\":\"" + employee.getMngId() + "\"\n}\n}").build();
            } else {
                return Response.ok("{\"error\":\"Department ID does not.\"}\n").build();
            }
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/employee")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateEmployee(String input) {
        business = new BusinessLayer();
        JsonReader rdr = Json.createReader(new StringReader(input));
        JsonObject obj = rdr.readObject();
        String company = obj.getString("company");
        if (!business.company(company)) {
            return Response.ok("{\"error\":\"Invalid company name. Company name must be RIT username.\"}\n").build();
        }
        try {
            int emp_id = obj.getInt("emp_id");
            String emp_name = obj.getString("emp_name");
            String emp_no = obj.getString("emp_no");
            String hire_date = obj.getString("hire_date");
            String job = obj.getString("job");
            double salary = Double.valueOf(obj.getInt("salary"));
            int departmentId = obj.getInt("dept_id");
            int managerId = obj.getInt("mng_id");
            business = new BusinessLayer();
            dl = new DataLayer(company);
            List<Department> departments = dl.getAllDepartment(company);
            List<Integer> departmentIdList = new ArrayList<Integer>();
            for (Department department : departments) {
                departmentIdList.add(department.getId());
            }
            if (business.deptID(departmentId, departmentIdList)) {
                List<Employee> employees = dl.getAllEmployee(company);
                List<String> employeeNoList = new ArrayList<String>();
                List<Integer> employeeIdList = new ArrayList<Integer>();
                for (Employee employee : employees) {
                    employeeNoList.add(employee.getEmpNo());
                    employeeIdList.add(employee.getId());
                }
                if (!business.employeeIds(emp_id, employeeIdList)) {
                    return Response.ok("{\"error\":\"Entered emp_id doesn't exists.\"}\n").build();
                }
                if (!business.manager(managerId, employeeIdList)) {
                    return Response.ok("{\"error\":\"Manager Id doesn't match any employees.\"}\n").build();
                }
                for (Employee employee : employees) {
                    if (business.employeeNumbers(emp_id, emp_no, employee)) {
                        return Response.ok(
                                "{\"error\":\"Employee Number already exists. Please try other employee number!! .\"}\n").build();
                    }
                }
                java.util.Date hireDateCheck = new SimpleDateFormat("yyyy-MM-dd").parse(hire_date);
                java.util.Date date = java.sql.Date.valueOf(LocalDate.now());
                SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
                String dayOfWeek = simpleDateformat.format(hireDateCheck);
                if (business.hireDate(hireDateCheck, date)) {
                    return Response.ok("{\"error\":\"Invalid Date: The date can not be after today.\"}\n").build();
                }
                if (business.weekend(dayOfWeek)) {
                    return Response
                            .ok("{\"error\":\"Not valid hire_date. The hire_date cannot be Saturday or Sunday.\"}\n").build();
                }
                java.sql.Date hireDateIn = new java.sql.Date(hireDateCheck.getTime());
                Employee employee = dl.getEmployee(emp_id);
                employee.setEmpName(emp_name);
                employee.setEmpNo(emp_no);
                employee.setHireDate(hireDateIn);
                employee.setJob(job);
                employee.setSalary(salary);
                employee.setDeptId(departmentId);
                employee.setMngId(managerId);
                employee = dl.updateEmployee(employee);
                return Response.ok("{\n\"success\":{\n\"emp_id\":" + employee.getId() + ",\n" + 
                                    "\"emp_name\":\""+ employee.getEmpName() + "\",\n" + 
                                    "\"emp_no\":\"" + employee.getEmpNo() + "\",\n" + 
                                    "\"hire_date\":\"" + employee.getHireDate() + "\",\n" + 
                                    "\"job\":\"" + employee.getJob()+ "\",\n" +
                                     "\"salary\":\"" + employee.getSalary() + "\",\n" +
                                      "\"dept_id\":\""+ employee.getDeptId() + "\",\n" + 
                                      "\"mng_id\":\"" + employee.getMngId() + "\"\n}\n}").build();
            } else {
                return Response.ok("{\"error\":\"There is no such dept_id.\"}\n").build();
            }
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/employee")
    @DELETE
    @Produces("application/json")
    public Response deleteEmployee(@QueryParam("company") String company, @QueryParam("emp_id") int emp_id) {
        try {
            dl = new DataLayer(company);
            int deletedEmployee = dl.deleteEmployee(emp_id);
            if (deletedEmployee <= 0) {
                return Response.ok("{\"error\":\"Employee with id: " + emp_id + " not found.\"}\n").build();
            } else {
                return Response.ok("{\"success\":\"Employee with id: " + emp_id + " deleted.\"}\n").build();
            }
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/timecard")
    @GET
    @Produces("application/json")
    public Response getTimecard(@QueryParam("company") String company, @QueryParam("timecard_id") int timecard_id) {
        try {
            dl = new DataLayer(company);
            Timecard tc = dl.getTimecard(timecard_id);
            if (tc != null) {
                return Response.ok("{\n\"timecard\":{\n\"timecard_id\":" + tc.getId() + ",\n"+ 
                                    "\"start_time\":\"" + tc.getStartTime() + "\",\n" +
                                     "\"end_time\":\"" + tc.getEndTime() + "\",\n" + 
                                     "\"emp_id\":\"" + tc.getEmpId() + "\"\n}\n}").build();
            } else {
                return Response.ok("{\"error\":\"no time card exists.\"}\n").build();
            }
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/timecards")
    @GET
    @Produces("application/json")
    public Response getAllTimecard(@QueryParam("company") String company,
                                     @QueryParam("emp_id") int emp_id) {
        try {
            dl = new DataLayer(company);
            List<Timecard> timecards = dl.getAllTimecard(emp_id);
            if (timecards.size() != 0) {
                List<String> timecardList = new ArrayList<String>();
                for (Timecard tc : timecards) {
                    timecardList.add("{\n\"timecard_id\":" + tc.getId() + ",\n" + 
                                    "\"start_time\":\""+ tc.getStartTime() + "\",\n" + 
                                    "\"end_time\":\"" + tc.getEndTime() + "\",\n"+ 
                                    "\"emp_id\":\"" + tc.getEmpId() + "\"\n}");
                }
                return Response.ok(timecardList.toString()).build();
            } else {
                return Response.ok("{\"error\":\"No time card exists.\"}\n").build();
            }
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }


    @Path("/timecard")
    @POST
    @Produces("application/json")
    public Response insertTimecard(@FormParam("company") String company,
                                    @FormParam("start_time") String start_time,
                                    @FormParam("end_time") String end_time, 
                                    @FormParam("emp_id") int emp_id) {
        business = new BusinessLayer();
        if (!business.company(company)) {
            return Response.ok("{\"error\":\"Company name not correct. Company name must be RIT username.\"}\n").build();
        }
        try
         {

            dl = new DataLayer(company);
            List<Employee> employees = dl.getAllEmployee(company);
            List<Integer> employeeIdList = new ArrayList<Integer>();
            for (Employee employee : employees) {
                employeeIdList.add(employee.getId());
            }
            if (!business.employeeIds(emp_id, employeeIdList)) {
                return Response.ok("{\"error\":\"Employee doesn't exists\"}\n").build();
            }

            Date sdfStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time);
            Timestamp startTimeTimeStamp = new Timestamp(sdfStart.getTime());
            Date sdfEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_time);
            Timestamp endTimeTimeStamp = new Timestamp(sdfEnd.getTime());
            Date startDate = new Date(startTimeTimeStamp.getTime());
            Date endDate = new Date(endTimeTimeStamp.getTime());
            Date now = java.sql.Date.valueOf(LocalDate.now());
            Calendar startCalender = Calendar.getInstance();
            startCalender.setTime(startTimeTimeStamp);
            Calendar endCalender = Calendar.getInstance();
            endCalender.setTime(endTimeTimeStamp);
            Calendar current = Calendar.getInstance();
            current.setFirstDayOfWeek(Calendar.MONDAY);
            current.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            current.set(Calendar.HOUR_OF_DAY, 0);
            current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);
            current.set(Calendar.MILLISECOND, 0);
            Date mondayBefore = current.getTime();

            if (!(startDate.after(mondayBefore) && startDate.before(now))) {
                return Response.ok("{\"error\":\"incorrect start date.\"}\n").build();
            }

            SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
            String startDayOfWeek = simpleDateformat.format(sdfStart);

            if (business.weekend(startDayOfWeek)) {
                return Response.ok("{\"error\":\"incorrect start day cannot be Saturday or Sunday.\"}\n").build();
            }

            if (business.hours(startCalender, endCalender)) {
                return Response.ok(
                        "{\"error\":\"incorrect start and endtime.\"}\n").build();
            }
            if (business.dayCheck(startDate, endDate)) {
                return Response.ok("{\"error\":\"start and end date must be same.\"}\n").build();
            }
            if (business.hourCheck(startDate, endDate)) {
                return Response.ok(
                        "{\"error\":\"incorrect endtime\"}\n").build();
            }

            List<Timecard> timecards = dl.getAllTimecard(emp_id);
            for (Timecard timecard : timecards) {
                Date timecardDate = new Date(timecard.getStartTime().getTime());
                if (business.newDay(timecardDate, startDate, timecard.getEmpId(), emp_id)) {
                    return Response.ok("{\"error\":\"invalid start date\"}\n").build();
                }
            }
            Timecard timecard = new Timecard(startTimeTimeStamp, endTimeTimeStamp, emp_id);
            timecard = dl.insertTimecard(timecard);
            return Response.ok("{\n\"success\":{\n\"timecard_id\":" + timecard.getId() + ",\n" +
                             "\"start_time\":\""+ timecard.getStartTime() + "\",\n" + 
                             "\"end_time\":\"" + timecard.getEndTime() + "\",\n" +
                              "\"emp_id\":\"" + timecard.getEmpId() + "\"\n}\n}").build();
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/timecard")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateTimecard(String timecardIn) {
        JsonReader reader = Json.createReader(new StringReader(timecardIn));
        JsonObject object = reader.readObject();
        String company = object.getString("company");
        business = new BusinessLayer();
        if (!business.company(company)) {
            return Response.ok("{\"error\":\"Invalid company name.Company must be valid RIT username\"}\n").build();
        }
        try {
            int timecardId = object.getInt("timecard_id");
            String start_time = object.getString("start_time");
            String end_time = object.getString("end_time");
            int emp_id = object.getInt("emp_id");
            business = new BusinessLayer();

            dl = new DataLayer(company);
            List<Employee> employees = dl.getAllEmployee(company);
            List<String> employeeNoList = new ArrayList<String>();
            List<Integer> employeeIdList = new ArrayList<Integer>();
            for (Employee employee : employees) {
                employeeIdList.add(employee.getId());
            }
            if (!business.employeeIds(emp_id, employeeIdList)) {
                return Response.ok("{\"error\":\"Employee ID doesn't exists\"}\n").build();
            }

            Date sdfStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time);
            Timestamp startTimeTimeStamp = new Timestamp(sdfStart.getTime());
            Date sdfEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_time);
            Timestamp endTimeTimeStamp = new Timestamp(sdfEnd.getTime());

            Date startDate = new Date(startTimeTimeStamp.getTime());
            Date endDate = new Date(endTimeTimeStamp.getTime());

            Date now = java.sql.Date.valueOf(LocalDate.now());

            Calendar startCalender = Calendar.getInstance();
            startCalender.setTime(startTimeTimeStamp);
            Calendar endCalender = Calendar.getInstance();
            endCalender.setTime(endTimeTimeStamp);
            Calendar current = Calendar.getInstance();
            current.setFirstDayOfWeek(Calendar.MONDAY);

            current.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            current.set(Calendar.HOUR_OF_DAY, 0);
            current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);
            current.set(Calendar.MILLISECOND, 0);

            Date mondayBefore = current.getTime();

            if (!(startDate.after(mondayBefore) && startDate.before(now))) {
                return Response.ok("{\"error\":\"in start date .\"}\n").build();
            }
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
            String startDayOfWeek = simpleDateformat.format(sdfStart);

            if (business.weekend(startDayOfWeek)) {
                return Response
                        .ok("{\"error\":\" Start date is not valid , should not be staturday or sunday.\"}\n").build();
            }

            if (business.hours(startCalender, endCalender)) {
                return Response.ok(
                        "{\"error\":\"start time and end time error\"}\n").build();
            }
            if (business.dayCheck(startDate, endDate)) {
                return Response.ok("{\"error\":\"End date must be same on the start date.\"}\n").build();
            }
            if (business.hourCheck(startDate, endDate)) {
                return Response.ok(
                        "{\"error\":\" END time is not valid should be 1 hour greater than start date\"}\n").build();
            }

            List<Timecard> timecards = dl.getAllTimecard(emp_id);
            List<Integer> timecardIdList = new ArrayList<Integer>();
            for (Timecard timecard : timecards) {
                timecardIdList.add(timecard.getId());
                Date timecardDate = new Date(timecard.getStartTime().getTime());
                if (business.newDay(timecardDate, startDate, timecard.getEmpId(), emp_id)) {
                    return Response.ok("{\"error\":\" start date is not valid\"}\n")
                            .build();
                } }

            if (business.timeCards(timecardId, timecardIdList)) {
                return Response.ok("{\"error\":\"Timecard not found.\"}\n").build();
            }
            Timecard timecard = dl.getTimecard(timecardId);
            timecard.setEmpId(emp_id);
            timecard.setStartTime(startTimeTimeStamp);
            timecard.setEndTime(endTimeTimeStamp);
            timecard = dl.updateTimecard(timecard);
            return Response.ok("{\n\"success\":{\n\"timecard_id\":" + timecard.getId() + ",\n" + 
                                "\"start_time\":\""+ timecard.getStartTime() + "\",\n" + 
                                "\"end_time\":\"" + timecard.getEndTime() + "\",\n" + 
                                "\"emp_id\":\"" + timecard.getEmpId() + "\"\n}\n}").build();
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

    @Path("/timecard")
    @DELETE
    @Produces("application/json")
    public Response deleteTimecard(@QueryParam("company") String company,
            @QueryParam("timecard_id") int timecard_id) {
        try {
            dl = new DataLayer(company);
            int row = dl.deleteTimecard(timecard_id);
            if (row <= 0) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.ok("{\"success\":\"Timecard with id: " + timecard_id + " deleted.\"}\n").build();
            }
        } catch (Exception e) {
            return Response.ok("{\"error\":\"" + e.getMessage() + "\"}\n").build();
        } finally {
            dl.close();
        }
    }

}