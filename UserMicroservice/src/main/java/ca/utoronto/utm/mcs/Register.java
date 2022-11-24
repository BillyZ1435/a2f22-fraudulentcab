package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 500
     * Register a user into the system using the given information.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {

        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String fields[] = {"name", "email", "password"};
        Class<?> fieldClasses[] = {String.class, String.class, String.class};
        if (!validateFields(body, fields, fieldClasses)) {
            this.sendStatus(r, 400);
            return;
        }
        String email = null;
        String name = null;
        String password = null;
        // check what values are present
        if (body.has("email")) {
            if (body.get("email").getClass() != String.class) {
                this.sendStatus(r, 400);
                return;
            }
            email = body.getString("email");
        }
        if (body.has("name")) {
            if (body.get("name").getClass() != String.class) {
                this.sendStatus(r, 400);
                return;
            }
            name = body.getString("name");
        }
        if (body.has("password")) {
            if (body.get("password").getClass() != String.class) {
                this.sendStatus(r, 400);
                return;
            }
            password = body.getString("password");
        }
        // bad request if email already exists in db?
        ResultSet emailCheckSet;
        boolean resultHasNext;
        try {
            emailCheckSet = this.dao.getUserFromEmail(email);
            resultHasNext = emailCheckSet.next();
            if(resultHasNext){
                this.sendStatus(r, 400);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
        try{
            this.dao.addUser(name, email, password);
            this.sendStatus(r, 200);
        }catch (SQLException e){
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

    }
}
