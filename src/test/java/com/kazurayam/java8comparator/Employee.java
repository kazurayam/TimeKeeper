package com.kazurayam.java8comparator;

public class Employee {
    String name;
    int age;
    double salary;
    long mobile;

    Employee(String name, int age, double salary, long mobile) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.mobile = mobile;
    }

    String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Employee)) {
            throw new IllegalArgumentException("obj is not Employee");
        }
        Employee other = (Employee)obj;
        return this.name == other.name &&
                this.age == other.age &&
                this.salary == other.salary &&
                this.mobile == other.mobile;
    }
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Employee(");
        sb.append("name=");
        sb.append(this.name);
        sb.append(", age=");
        sb.append(this.age);
        sb.append(", salary=");
        sb.append(this.salary);
        sb.append(", mobile=");
        sb.append(this.mobile);
        sb.append(")");
        return sb.toString();
    }
}
