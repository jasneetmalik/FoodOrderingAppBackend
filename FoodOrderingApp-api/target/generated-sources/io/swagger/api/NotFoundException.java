package io.swagger.api;

<<<<<<< HEAD
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2021-03-23T19:33:23.670+05:30")
=======
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2021-03-24T20:57:37.744-04:00")
>>>>>>> master

public class NotFoundException extends ApiException {
    private int code;
    public NotFoundException (int code, String msg) {
        super(code, msg);
        this.code = code;
    }
}
