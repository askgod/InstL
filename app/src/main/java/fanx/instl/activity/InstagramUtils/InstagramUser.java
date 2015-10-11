package fanx.instl.activity.InstagramUtils;

/**
 * Created by SShrestha on 10/10/15.
 */
public class InstagramUser {

    public String id;
    public String username;
    public String first_name;
    public String last_name;
    public String profile_picture;

    public String toString(){
        return "USERID: "+id+"\nUsername: "+username+"\nFirstname: "+ first_name+"\nLastname: "+last_name+"\nProfile Pic: "+profile_picture;
    }

}
