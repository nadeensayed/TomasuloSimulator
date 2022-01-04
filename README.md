# TomasuloSimulator

**Development Process**:
The first step in the development of our code was to decide the architecture of
our Tomasulo simulator. We have 2 reservation stations for ADD/SUB operations,
2 reservation stations for MUL/DIV operations, 2 load buffers and 2 store buffers.
Then, we chose the data structure for each component:
 Our memory is a double array with capacity of 200.
 Created a class called reservationStation which contains all the attributes of
a reservation station (busy, QJ, op, etc…)
 Created a class called register which contains the Q and the value of each
register
 The register file is an array of register class with capacity 32
 Each of the 4 reservation stations (ADD/SUB, MUL/DIV, LD and SD) is an
array of reservationStation class with capacity of 2
The last step of the development process was to make some assumptions. The
first assumption was that the loads and store can access the memory in the same
cycle. The second assumption was that only one instruction can write back in a
cycle and that instruction is chosen randomly.

**Running Process**:
Our class is named simulator. The first thing that happens in the constructor is
initializing all registers and reservation stations. Then we call a method called
simulate. In this method the assembly code instructions are scanned from a text
file and put in a queue. The latencies of each instruction type is also scanned from
a text file. Then in this method there is while loop which resembles the clock ticks.
The while loop is only exited when there are no other instructions in the queue
and all reservation stations are empty meaning the code finished executing and
writing back and no other instructions to issue. Inside the loop three other 
methods are called which are the issue, execute and writeBack. In the issue
method it checks the type of instruction at the head of the queue and check
whether there is a free slot in the corresponding reservation station. If there is a
free slot then the instruction is popped from the queue otherwise the queue is
stalled this cycle. In the execute method it checks all busy reservation stations and
decrement the remaining time of execution for each instruction. In the write back
method it checks each instruction in the reservation station that are ready to
write back and only chooses one to write back this cycle and updates all other
reservation stations and register file waiting for this instruction to finish. Then at
the end of the loop the number of cycles is incremented.

**Testing Process**:
These are the test cases we used to test our code with latencies:
ADD and SUB =3
MUL and DIV =2
LD and SD =1
LD F0 100
ADD F2 F0 F0

LD F0 100
LD F1 101
ADD F2 F1 F0
ADD F2 F2 F0
ADD F2 F0 F0

LD F0 100
LD F1 101
ADD F2 F1 F0
DIV F2 F2 F0
MUL F0 F0 F0
SD F2 100

LD F0 100
LD F1 101
LD F20 102
MUL F0 F0 F20
MUL F1 F1 F20
ADD F2 F0 F1
ADD F20 F20 F20
SD F2 102