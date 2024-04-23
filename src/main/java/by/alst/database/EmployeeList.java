package by.alst.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmployeeList {
    private String info;
    private List<Employee> employees = new ArrayList<>();

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeList that = (EmployeeList) o;
        return Objects.equals(info, that.info) && Objects.equals(employees, that.employees);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, employees);
    }

    @Override
    public String toString() {
        return "EmployeeList{" +
                "info='" + info + '\'' +
                ", employees=" + employees +
                '}';
    }
}






