# Getting Started

### Algorithm
Algorithm of the lifts is FCFS when calling a lift. The closest lift is called fo answer the call. When the lift is coming down and somebody form lower level will call the lift than it will take this call, but this not works when:
* The lift is in call and go mode (between the call mode when lift is picking up the passenger and go mode when it is decided which direction the lift will go), if the lift will continue down than the status will be CONTINUING_DOWN, which preventing form disturbing the flow of lift

Additionally, there is a queue of waiting "calls" if all the lifts are busy then the call will go to the queue and the first free lift will take it.

### Program main functionality
Whan enteling program in the **localhost:8080** there are few different components. Firstly the textboxes are a setup for program. There we will write the number of lifts and initial maximum floor number for all the lifts. Lower is the table on which the information for the lifts are printed
* Lifts id
* Current floor of the lift
* Target floor of the lift
* Status

And then we have 2 rows of options
* ComboBox with "call" button. Calls closest lift to the target floor
* Two ComboBoxes with  "go to" button. The lift is called to first floor waits for 2s and continues to the second floor

### Additional functionality
Context menu was added after clicking the table with right button on the mouse it allows to use:
* Refresh - refreshes the table updating columns with currentFloor, targetFloor and status
* Edit - allows user to edit the max floor number and min floor number to the lift that was chosen from the list
* Broke - sets status of the chosen lift to **BROKEN** excluding it from functioning
* Repair - repairs the BROKEN lift and setting status into **NOT_MOVING**

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.5/maven-plugin/reference/html/#build-image)
* [Vaadin](https://vaadin.com/docs)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/index.html#using.devtools)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)


