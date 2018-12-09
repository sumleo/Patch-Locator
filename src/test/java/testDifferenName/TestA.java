package testDifferenName;

public class TestA {
    private String name;
    private int age;
    private String city;

    public String getName() {
        return name;
    }

    public TestA() {
        System.out.println();
    }

    public TestA(String str){
        System.out.println(str);
    }


    public void setName(String name) {
        String a = "1";
        this.name = name;
    }
    public int getAgeX(String a,String b) {
        return age;
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
