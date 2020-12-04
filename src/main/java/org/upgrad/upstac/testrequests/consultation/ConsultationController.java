package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);
    @Autowired
    TestRequestFlowService testRequestFlowService;
    @Autowired
    private TestRequestUpdateService testRequestUpdateService;
    @Autowired
    private TestRequestQueryService testRequestQueryService;
    @Autowired
    private UserLoggedInService userLoggedInService;


    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations() {

        return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED); //used findBy() method from testRequestQueryService class to get the list of test requests having status as 'LAB_TEST_COMPLETED'

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor() {

        User doctor = userLoggedInService.getLoggedInUser(); //created an object of User class Named "doctor" and stored the current logged in user using userLoggedInService
        return testRequestQueryService.findByDoctor(doctor); //used findByDoctor() method from testRequestQueryService class and returned the User parameter

    }


    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {

        try {
            User doctor = userLoggedInService.getLoggedInUser(); //created an object of User class Named "doctor" and stored the current logged in user using userLoggedInService
            return testRequestUpdateService.assignForConsultation(id, doctor); //used assignForConsultation() method of testRequestUpdateService and returned the id and User parameters
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id, @RequestBody CreateConsultationRequest testResult) {

        try {
            User doctor = userLoggedInService.getLoggedInUser(); //created an object of User class Named "doctor" and stored the current logged in user using userLoggedInService
            return testRequestUpdateService.updateConsultation(id, testResult, doctor); //used updateConsultation() method from testRequestUpdateService class and returned the id, tesResult and User parameters
        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


}
