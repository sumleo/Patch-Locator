package Utils;

public class TestA {
    private String name;
    private int age;
    private String city;

    public String getName() {
        return name;
    }

    public TestA() {
        System.out.println(1);
    }

    public void setName(String name) {
        String a = "1";
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
