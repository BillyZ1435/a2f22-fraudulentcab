package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the 
     * information of the user in the database.
     */
    
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {

        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String fields[] = {"email", "password"};
        Class<?> fieldClasses[] = {String.class, String.class};
        if (!validateFields(body, fields, fieldClasses)) {
            this.sendStatus(r, 400);
            return;
        }
        String email = null;
        String password = null;
        if (body.has("email")) {
            if (body.get("email").getClass() != String.class) {
                this.sendStatus(r, 400);
                return;
            }
            email = body.getString("email");
        }
        if (body.has("password")) {
            if (body.get("password").getClass() != String.class) {
                this.sendStatus(r, 400);
                return;
            }
            password = body.getString("password");
        }
        // get the guy by email
        ResultSet emailCheckSet;
        boolean resultHasNext;
        try {
            emailCheckSet = this.dao.getUserFromEmail(email);
            resultHasNext = emailCheckSet.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
        // check if user with given email exists, return 404 if not
        if (!resultHasNext) {
            this.sendStatus(r, 404);
            return;
        }
        try{
            if(!password.equals(emailCheckSet.getString("password"))){
                System.out.println(emailCheckSet.getString("password")+password);
                this.sendStatus(r, 401);
            }else{
                this.sendStatus(r, 200);
            }
        }catch (SQLException e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }


    }
}
