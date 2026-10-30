package org.kelompokwira.wirakopi.wirakopi.Service;

import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.kelompokwira.wirakopi.wirakopi.Entity.User;
import org.kelompokwira.wirakopi.wirakopi.Repository.WiraRepo;
import org.kelompokwira.wirakopi.wirakopi.WirakopiApplication.Something;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@org.springframework.context.annotation.Profile("!demo & !test")
@Service
public class UserServiceImpl implements UserService {

    private WiraRepo userRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder encoder;

    public UserServiceImpl(WiraRepo userRepo) {
        super();
        this.userRepo = userRepo;
    }

    @Override
    public String register(User user) {
        try {
            if(!userRepo.findByUsername(user.getUsername()).isEmpty()) return "Username has taken";
            userRepo.save(user);
        } catch (Exception e) {return e.getMessage();}
        return "Success";       
    }

    @Override
    public String changeSomething(Something something ,String valueWannaChange, String username) {
        try{
            List<User> users = userRepo.findByUsername(username);
            User changeUser = !users.isEmpty() ? users.getFirst() : null;
            if (changeUser == null) return "Username is not found, if you want to change password consider search your username through email";
            switch (something) {
                case Password:
                    String passString = encoder.encode(valueWannaChange);
                    if(changeUser.getPassword() == passString)
                        return "You put the same password as now, are you ok?";
                    changeUser.setPassword(passString);
                    break;
                case Email:
                    if(!EmailValidator.getInstance().isValid(valueWannaChange))
                        return "Invalid email, please return with @google.com or @hotmail.com or etc";
                    if(changeUser.getEmail() == valueWannaChange)
                        return "You put the correct email, are you ok?";
                    if(changeUser.getPassword() == encoder.encode(valueWannaChange))
                        return "You...... put your password inside change email..... Seriously, are you ok?";
                    changeUser.setEmail(valueWannaChange);
                    break;
                default:
                    return "What?";
            }
            userRepo.save(changeUser);
            return "Success";
        }catch(Exception e){return e.getMessage();}
    }

    @Override
    public Iterable<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public boolean login(String password, String userEmail) {
        // List<User> userOptional = userRepo.findByUsername(userEmail);
        // if(userOptional.isEmpty()) return false;
        // if(userOptional.getFirst().getPassword() != DigestUtils.sha256Hex(password))
        //     return false;
        return true;
    }

    

    /**
     *  This is deprecated because you take all data to database which is take long time
     *  
     * @return Iterable/List all of user credential
     * @since 0.0.1
     */
    @Deprecated
    @Override
    public Iterable<User> getAllUser() {
        return userRepo.findAll();
    }

    @Override
    public Iterable<String> allUsernamebyEmail(String email) {
        List<User> Users = userRepo.findByEmail(email);
        List<String> ofUsername = new ArrayList<String>();
        for (User User : Users) 
            ofUsername.add(User.getUsername());
        return ofUsername;

    }

    @Override
    public String sendOTP(String email, String randomShit) {
        try {
            if(!EmailValidator.getInstance().isValid(email))
                return "Invalid email, please return with @google.com or @hotmail.com or etc";
            SimpleMailMessage send = new SimpleMailMessage();
            send.setFrom("fallrehan@gmail.com");
            send.setTo(email);
            send.setSubject("Your OTP has arrive");
            send.setText("Yout OTP is: " + randomShit);
            mailSender.send(send);
            return "Complete";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    
}
