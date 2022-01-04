import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;



public class simulator {
	
	 static double [] memory=new double [200];
	 static reservationStation[]A=new reservationStation[2];
	 static reservationStation[]M=new reservationStation[2];
	 static reservationStation[]L=new reservationStation[2];
	 static reservationStation[]S=new reservationStation[2];
	 static register[] registerFile=new register[32];
	 
	 static int add;
	 static int sub;
	 static int mul;
	 static int div;
	 static int ld;
	 static int sd;
	 
	 public simulator() throws FileNotFoundException {
		 
		 A[0]= new reservationStation();
		 A[1]= new reservationStation();
		 M[0]= new reservationStation();
		 M[1]= new reservationStation();
		 L[0]= new reservationStation();
		 L[1]= new reservationStation();
		 S[0]= new reservationStation();
		 S[1]= new reservationStation();
		 for (int i=0;i<32;i++) {
			 registerFile[i]=new register();
		 }
		 memory[100]=3;
		 memory[101]=5;
		 memory[102]=2;
		 registerFile[20].value=10;
		 String program = "src/" + "latency" + ".txt";
	     File file =new File(program);
	     Scanner sc = new Scanner(file);
	     while (sc.hasNextLine()){
	    	 
	        String s=sc.nextLine();
	        String [] l= s.split(" ");
	        add=Integer.parseInt(l[0]);
	        sub=Integer.parseInt(l[1]);
	        mul=Integer.parseInt(l[2]);
	        div=Integer.parseInt(l[3]);
	        ld=Integer.parseInt(l[4]);
	        sd=Integer.parseInt(l[5]);
	        
	   }
	     sc.close();
		 
		 
	 }
	 
	 
	public void simulate(String code) throws FileNotFoundException {
		
	    Queue<instruction> instructions =new LinkedList<instruction>();
		String program = "src/" + code + ".txt";
        File file =new File(program);
        Scanner sc = new Scanner(file);
		int num=1;
        while (sc.hasNextLine()){
        	String s=sc.nextLine();
        	String [] ins= s.split(" ");
        	if(ins[0].equals("LD")||ins[0].equals("SD")) {
        		instruction i= new instruction(num,ins[0],ins[1],Integer.parseInt(ins[2]));
        		instructions.add(i);
        	}
        	else {
        		instruction i= new instruction(num,ins[0],ins[1],ins[2],ins[3]);
        		instructions.add(i);
        	}
        	num++;
        }
        sc.close();	
        int cycle=1;
        boolean done=false;
        while(!instructions.isEmpty()||!done) {
        	System.out.println("------------------------------------------------------------------------------------------------------");
        	System.out.println("Cycle "+cycle+" :");
        	done =false;
        	execute(cycle);
        	writeBack(cycle);
        	issue(cycle,instructions);
        	
        	System.out.println("A[0] : "+A[0]);
        	System.out.println("A[1] : "+A[1]);
        	System.out.println("");
        	System.out.println("M[0] : "+M[0]);
        	System.out.println("M[1] : "+M[1]);
        	System.out.println("");
        	System.out.println("L[0] : "+L[0].toString2());
        	System.out.println("L[1] : "+L[1].toString2());
        	System.out.println("");
        	System.out.println("S[0] : "+S[0].toString3());
        	System.out.println("S[1] : "+S[1].toString3());
        	System.out.println("");
        	System.out.println("Register File:");
        	for(int i=0;i<32;i++) {
        		System.out.println("F"+i +" :"+"Q: "+registerFile[i].Q+" Value: "+ registerFile[i].value);
        	}
        	cycle++;
        	if(A[0].busy==0&&A[1].busy==0&&M[0].busy==0&& M[1].busy==0&&L[0].busy==0&& L[1].busy==0&& S[0].busy==0&&S[1].busy==0) {
        		done=true;
        	}
        	 
        	
        	
        }
        
        System.out.println(memory[102]);
      

	}
	
	public static void execute(int cycle) {
		
		if(A[0].busy==1&& A[0].Qj.equals("")&&A[0].Qk.equals("")&&A[0].time!=-1) {
			System.out.println();
			A[0].time--;
		}
		if(A[1].busy==1&& A[1].Qj.equals("")&&A[1].Qk.equals("")&&A[1].time!=-1) {
			A[1].time--;
		}
		if(M[0].busy==1&& M[0].Qj.equals("")&&M[0].Qk.equals("")&&M[0].time!=-1) {
			M[0].time--;
		}
		if(M[1].busy==1&& M[1].Qj.equals("")&&M[1].Qk.equals("")&&M[1].time!=-1) {
			M[1].time--;
		}
		
		
		if(L[0].busy==1 && L[0].time!=-1) {
			L[0].time--;
		}
		if(L[1].busy==1&& L[1].time!=-1) {
			L[1].time--;
		}
		
		if(S[0].busy==1&& S[0].Qj.equals("")&&S[0].time!=-1) {
			S[0].time--;
		}
		if(S[1].busy==1&& S[1].Qj.equals("")&&S[1].time!=-1) {
			S[1].time--;
		}
		
		
		
	}
	
	
	public static void writeBack(int cycle) {
		boolean wrote=false;
		
		if(A[0].busy==1&& A[0].Qj.equals("")&&A[0].Qk.equals("")&&A[0].time==-1) {
			System.out.println("Instruction "+A[0].num+" wrote back");
			double result;
			
			if(A[0].op.equals("ADD"))
				result =A[0].Vj+A[0].Vk;
			else
				result =A[0].Vj-A[0].Vk;
			
			A[0].busy=0;
			A[0].time=-1;
			A[0].num=0;
			A[0].op="";
			wrote=true;
			for (int i=0;i<32;i++) {
				if(registerFile[i].Q.equals("A0")) {
					registerFile[i].value=result;
					registerFile[i].Q="0";
				}
			}
			if(A[1].busy==1&&A[1].Qj.equals("A0")) {
				A[1].Vj=result;
				A[1].Qj="";
			}
			if(A[1].busy==1&&A[1].Qk.equals("A0")) {
				A[1].Vk=result;
				A[1].Qk="";
			}
			if(M[0].busy==1&&M[0].Qj.equals("A0")) {
				M[0].Vj=result;
				M[0].Qj="";
			}
			if(M[0].busy==1&&M[0].Qk.equals("A0")) {
				M[0].Vk=result;
				M[0].Qk="";
			}
			if(M[1].busy==1&&M[1].Qj.equals("A0")) {
				M[1].Vj=result;
				M[1].Qj="";
			}
			if(M[1].busy==1&&M[1].Qk.equals("A0")) {
				M[1].Vk=result;
				M[1].Qk="";
			}
			if(S[0].busy==1&&S[0].Qj.equals("A0")) {
				S[0].Vj=result;
				S[0].Qj="";
			}
			
			if(S[1].busy==1&&S[1].Qj.equals("A0")) {
				S[1].Vj=result;
				S[1].Qj="";
			}
			
			
			
			
		}
		if(A[1].busy==1&& A[1].Qj.equals("")&&A[1].Qk.equals("")&&A[1].time==-1 &&!wrote) {
	        double result;
	        System.out.println("Instruction "+A[1].num+" wrote back");
			if(A[1].op.equals("ADD"))
				result =A[1].Vj+A[1].Vk;
			else
				result =A[1].Vj-A[1].Vk;
			
			A[1].busy=0;
			A[1].time=-1;
			A[1].num=0;
			A[1].op="";
			wrote=true;
			for (int i=0;i<32;i++) {
				if(registerFile[i].Q.equals("A1")) {
					registerFile[i].value=result;
					registerFile[i].Q="0";
				}
			}
			if(A[0].busy==1&&A[0].Qj.equals("A1")) {
				A[0].Vj=result;
				A[0].Qj="";
			}
			if(A[0].busy==1&&A[0].Qk.equals("A1")) {
				A[0].Vk=result;
				A[0].Qk="";
			}
			if(M[0].busy==1&&M[0].Qj.equals("A1")) {
				M[0].Vj=result;
				M[0].Qj="";
			}
			if(M[0].busy==1&&M[0].Qk.equals("A1")) {
				M[0].Vk=result;
				M[0].Qk="";
			}
			if(M[1].busy==1&&M[1].Qj.equals("A1")) {
				M[1].Vj=result;
				M[1].Qj="";
			}
			if(M[1].busy==1&&M[1].Qk.equals("A1")) {
				M[1].Vk=result;
				M[1].Qk="";
			}
			if(S[0].busy==1&&S[0].Qj.equals("A1")) {
				S[0].Vj=result;
				S[0].Qj="";
			}
			
			if(S[1].busy==1&&S[1].Qj.equals("A1")) {
				S[1].Vj=result;
				S[1].Qj="";
			}
		}
		
		
		
		
		
		if(M[0].busy==1&& M[0].Qj.equals("")&&M[0].Qk.equals("")&&M[0].time==-1 &&!wrote) {
	        double result;
	        System.out.println("Instruction "+M[0].num+" wrote back");
			if(M[0].op.equals("MUL"))
				result =M[0].Vj*M[0].Vk;
			else
				result =M[0].Vj/M[0].Vk;
			
			M[0].busy=0;
			M[0].time=-1;
			M[0].num=0;
			M[0].op="";
			wrote=true;
			for (int i=0;i<32;i++) {
				if(registerFile[i].Q.equals("M0")) {
					registerFile[i].value=result;
					registerFile[i].Q="0";
				}
			}
			if(A[0].busy==1&&A[0].Qj.equals("M0")) {
				A[0].Vj=result;
				A[0].Qj="";
			}
			if(A[0].busy==1&&A[0].Qk.equals("M0")) {
				A[0].Vk=result;
				A[0].Qk="";
			}
			if(A[1].busy==1&&A[1].Qj.equals("M0")) {
				A[1].Vj=result;
				A[1].Qj="";
			}
			if(A[1].busy==1&& A[1].Qk.equals("M0")) {
				A[1].Vk=result;
				A[1].Qk="";
			}
			if(M[1].busy==1&&M[1].Qj.equals("M0")) {
				M[1].Vj=result;
				M[1].Qj="";
			}
			if(M[1].busy==1&&M[1].Qk.equals("M0")) {
				M[1].Vk=result;
				M[1].Qk="";
			}
			if(S[0].busy==1&&S[0].Qj.equals("M0")) {
				S[0].Vj=result;
				S[0].Qj="";
			}
			
			if(S[1].busy==1&&S[1].Qj.equals("M0")) {
				S[1].Vj=result;
				S[1].Qj="";
			}
		}
		if(M[1].busy==1&& M[1].Qj.equals("")&&M[1].Qk.equals("")&&M[1].time==-1 &&!wrote) {
	        double result;
	        System.out.println("Instruction "+M[1].num+" wrote back");
			if(M[1].op.equals("MUL"))
				result =M[1].Vj*M[1].Vk;
			else
				result =M[1].Vj/M[1].Vk;
			
			M[1].busy=0;
			M[1].time=-1;
			M[1].num=0;
			M[1].op="";
			wrote=true;
			for (int i=0;i<32;i++) {
				if(registerFile[i].Q.equals("M1")) {
					registerFile[i].value=result;
					registerFile[i].Q="0";
				}
			}
			if(A[0].busy==1&&A[0].Qj.equals("M1")) {
				A[0].Vj=result;
				A[0].Qj="";
			}
			if(A[0].busy==1&&A[0].Qk.equals("M1")) {
				A[0].Vk=result;
				A[0].Qk="";
			}
			if(A[1].busy==1&& A[1].Qj.equals("M1")) {
				A[1].Vj=result;
				A[1].Qj="";
			}
			if(A[1].busy==1&& A[1].Qk.equals("M1")) {
				A[1].Vk=result;
				A[1].Qk="";
			}
			if(M[0].busy==1&&M[0].Qj.equals("M1")) {
				M[0].Vj=result;
				M[0].Qj="";
			}
			if(M[0].busy==1&&M[0].Qk.equals("M1")) {
				M[0].Vk=result;
				M[0].Qk="";
			}
			if(S[0].busy==1&&S[0].Qj.equals("M1")) {
				S[0].Vj=result;
				S[0].Qj="";
			}
			
			if(S[1].busy==1&&S[1].Qj.equals("M1")) {
				S[1].Vj=result;
				S[1].Qj="";
			}
		}
		
		
		if(S[0].busy==1 && S[0].Qj.equals("")&& S[0].time==-1) {
			System.out.println("Instruction "+S[0].num+" wrote back");
			memory[S[0].address]= S[0].Vj;
			S[0].busy=0;
			S[0].address=-1;
			S[0].num=0;
			S[0].time=-1;
		}
		
		if(S[1].busy==1 && S[1].Qj.equals("")&& S[1].time==-1) {
			System.out.println("Instruction "+S[1].num+" wrote back");
			memory[S[1].address]= S[1].Vj;
			S[1].busy=0;
			S[1].address=-1;
			S[1].num=0;
			S[1].time=-1;
		}
		
		if(L[0].busy==1&&L[0].time==-1 && !wrote) {
			System.out.println("Instruction "+L[0].num+" wrote back");
            double result= memory[L[0].address];
            L[0].busy= 0;
            L[0].address= -1;
            L[0].num= 0;
            L[0].time= -1;
			wrote=true;
			
			for (int i=0;i<32;i++) {
				if(registerFile[i].Q.equals("L0")) {
					registerFile[i].value=result;
					registerFile[i].Q="0";
				}
			}
			if(A[0].busy==1&&A[0].Qj.equals("L0")) {
				A[0].Vj=result;
				A[0].Qj="";
			}
			if(A[0].busy==1&&A[0].Qk.equals("L0")) {
				A[0].Vk=result;
				A[0].Qk="";
			}
			if(A[1].busy==1&&A[1].Qj.equals("L0")) {
				A[1].Vj=result;
				A[1].Qj="";
			}
			if(A[1].busy==1&& A[1].Qk.equals("L0")) {
				A[1].Vk=result;
				A[1].Qk="";
			}
			if(M[0].busy==1&&M[0].Qj.equals("L0")) {
				M[0].Vj=result;
				M[0].Qj="";
			}
			if(M[0].busy==1&&M[0].Qk.equals("L0")) {
				M[0].Vk=result;
				M[0].Qk="";
			}
			if(M[1].busy==1&&M[1].Qj.equals("L0")) {
				M[1].Vj=result;
				M[1].Qj="";
			}
			if(M[1].busy==1&&M[1].Qk.equals("L0")) {
				M[1].Vk=result;
				M[1].Qk="";
			}
			if(S[0].busy==1&&S[0].Qj.equals("L0")) {
				S[0].Vj=result;
				S[0].Qj="";
			}
			
			if(S[1].busy==1&&S[1].Qj.equals("L0")) {
				S[1].Vj=result;
				S[1].Qj="";
			}
			
		}
		
		if(L[1].busy==1&&L[1].time==-1 && !wrote) {
			System.out.println("Instruction "+L[1].num+" wrote back");
            double result= memory[L[1].address];
            L[1].busy= 0;
            L[1].address= -1;
            L[1].num= 0;
            L[1].time= -1;
			wrote=true;
			
			for (int i=0;i<32;i++) {
				if(registerFile[i].Q.equals("L1")) {
					registerFile[i].value=result;
					registerFile[i].Q="0";
				}
			}
			if(A[0].busy==1&&A[0].Qj.equals("L1")) {
				A[0].Vj=result;
				A[0].Qj="";
			}
			if(A[0].busy==1&&A[0].Qk.equals("L1")) {
				A[0].Vk=result;
				A[0].Qk="";
			}
			if(A[1].busy==1&&A[1].Qj.equals("L1")) {
				A[1].Vj=result;
				A[1].Qj="";
			}
			if(A[1].busy==1&&A[1].Qk.equals("L1")) {
				A[1].Vk=result;
				A[1].Qk="";
			}
			if(M[0].busy==1&&M[0].Qj.equals("L1")) {
				M[0].Vj=result;
				M[0].Qj="";
			}
			if(M[0].busy==1&&M[0].Qk.equals("L1")) {
				M[0].Vk=result;
				M[0].Qk="";
			}
			if(M[1].busy==1&&M[1].Qj.equals("L1")) {
				M[1].Vj=result;
				M[1].Qj="";
			}
			if(M[1].busy==1&&M[1].Qk.equals("L1")) {
				M[1].Vk=result;
				M[1].Qk="";
			}
			if(S[0].busy==1&&S[0].Qj.equals("L1")) {
				S[0].Vj=result;
				S[0].Qj="";
			}
			
			if(S[1].busy==1&&S[1].Qj.equals("L1")) {
				S[1].Vj=result;
				S[1].Qj="";
			}
			
		}
	}
	
	
	
	public static void issue(int cycle,Queue<instruction> instructions) {
		
		if(!instructions.isEmpty()) {
			
			instruction i= instructions.peek();
			
			
			if(i.op.equals("ADD")||i.op.equals("SUB")) {
				if(A[0].busy==0) {
					i=instructions.remove();
					System.out.println("Instruction "+i.num+" was issued");
					A[0].busy=1;
					A[0].num=i.num;
					A[0].op=i.op;
					
					int j=Integer.parseInt(i.j.substring(1));
					int k=Integer.parseInt(i.k.substring(1));
					if(registerFile[j].Q.equals("0")) {
						A[0].Vj=registerFile[j].value;
						
					}
					else {
						A[0].Qj=registerFile[j].Q;
					}
					
					if(registerFile[k].Q.equals("0")) {
						A[0].Vk=registerFile[k].value;
						
					}
					else {
						A[0].Qk=registerFile[k].Q;
					}
					int r=Integer.parseInt(i.r.substring(1));
					registerFile[r].Q="A0";
					if(i.op.equals("ADD")) {
						A[0].time=add;
					}
					else {
						A[0].time=sub;
					}
					
				}
				else if(A[1].busy==0) {
					i=instructions.remove();
					System.out.println("Instruction "+i.num+" was issued");
					A[1].busy=1;
					A[1].num=i.num;
					A[1].op=i.op;
					
					int j=Integer.parseInt(i.j.substring(1));
					int k=Integer.parseInt(i.k.substring(1));
					if(registerFile[j].Q.equals("0")) {
						A[1].Vj=registerFile[j].value;
						
					}
					else {
						A[1].Qj=registerFile[j].Q;
					}
					
					if(registerFile[k].Q.equals("0")) {
						A[1].Vk=registerFile[k].value;
						
					}
					else {
						A[1].Qk=registerFile[k].Q;
					}
					int r=Integer.parseInt(i.r.substring(1));
					registerFile[r].Q="A1";
					if(i.op.equals("ADD")) {
						A[1].time=add;
					}
					else {
						A[1].time=sub;
					}
				}
				
			
			}
			
			
			
			else if(i.op.equals("MUL")||i.op.equals("DIV")) {
				
				if(M[0].busy==0) {
					i=instructions.remove();
					System.out.println("Instruction "+i.num+" was issued");
					M[0].busy=1;
					M[0].num=i.num;
					M[0].op=i.op;
					
					int j=Integer.parseInt(i.j.substring(1));
					int k=Integer.parseInt(i.k.substring(1));
					if(registerFile[j].Q.equals("0")) {
						M[0].Vj=registerFile[j].value;
						
					}
					else {
						M[0].Qj=registerFile[j].Q;
					}
					
					if(registerFile[k].Q.equals("0")) {
						M[0].Vk=registerFile[k].value;
						
					}
					else {
						M[0].Qk=registerFile[k].Q;
					}
					int r=Integer.parseInt(i.r.substring(1));
					registerFile[r].Q="M0";
					if(i.op.equals("MUL")) {
						M[0].time=mul;
					}
					else {
						M[0].time=mul;
					}
					
				}
				else if(M[1].busy==0) {
					i=instructions.remove();
					System.out.println("Instruction "+i.num+" was issued");
					M[1].busy=1;
					M[1].num=i.num;
					M[1].op=i.op;
					
					int j=Integer.parseInt(i.j.substring(1));
					int k=Integer.parseInt(i.k.substring(1));
					if(registerFile[j].Q.equals("0")) {
						M[1].Vj=registerFile[j].value;
						
					}
					else {
						M[1].Qj=registerFile[j].Q;
					}
					
					if(registerFile[k].Q.equals("0")) {
						M[1].Vk=registerFile[k].value;
						
					}
					else {
						M[1].Qk=registerFile[k].Q;
					}
					int r=Integer.parseInt(i.r.substring(1));
					registerFile[r].Q="M1";
					if(i.op.equals("MUL")) {
						M[1].time=mul;
					}
					else {
						M[1].time=mul;
					}
				}
				
			}
			else if(i.op.equals("LD")) {
				
				if(L[0].busy==0) {
					i=instructions.remove();
					System.out.println("Instruction "+i.num+" was issued");
					L[0].busy=1;
					L[0].num=i.num;
					L[0].address=i.address;
					L[0].time=ld;
					int r=Integer.parseInt(i.r.substring(1));
					registerFile[r].Q="L0";
				}
				else if(L[1].busy==0) {
					i=instructions.remove();
					System.out.println("Instruction "+i.num+" was issued");
					L[1].busy=1;
					L[1].num=i.num;
					L[1].address=i.address;
					L[1].time=ld;
					int r=Integer.parseInt(i.r.substring(1));
					registerFile[r].Q="L1";
				}
			
			
				
			}
			else {
				
				if(S[0].busy==0) {
					i=instructions.remove();
					System.out.println("Instruction "+i.num+" was issued");
					S[0].busy=1;
					S[0].num=i.num;
					S[0].address=i.address;
					S[0].time=sd;
					int r=Integer.parseInt(i.r.substring(1));
					if(registerFile[r].Q.equals("0")) {
						S[0].Vj=registerFile[r].value;
						
					}
					else {
						S[0].Qj=registerFile[r].Q;
					}
					
					
					
				}
				else if(S[0].busy==0) {
					i=instructions.remove();
					System.out.println("Instruction "+i.num+" was issued");
					S[1].busy=1;
					S[1].num=i.num;
					S[1].address=i.address;
					S[1].time=sd;
					int r=Integer.parseInt(i.r.substring(1));
					if(registerFile[r].Q.equals("0")) {
						S[1].Vj=registerFile[r].value;
						
					}
					else {
						S[1].Qj=registerFile[r].Q;
					}
				}
				
				
			}
			
			
			
			
			
			
			
		}
		
		
		
		
		
	}
	
	
	public static void main(String []args) throws FileNotFoundException {
			
		  simulator s=new simulator();
		  s.simulate("code");
    
	}

}






class instruction{
	
	int num;
	String op;
	String r;
	String j;
	String k;
	int address;
	
	public instruction(int num,String op,String r,String j, String k) {
		this.num=num;
		this.op=op;
		this.r=r;
		this.j=j;
		this.k=k;
	}
	public instruction(int num,String op,String r,int address) {
		this.num=num;
		this.op=op;
		this.r=r;
		this.address=address;
	}
	
	
	
}

class reservationStation{
	
	int busy=0;
	int time=-1;
	int num=0;
	String op="";
	double Vj=0;
	double Vk=0;
	String Qj="";
	String Qk="";
	int address=-1;
	
	public String toString() {
		return "Op: " + op + " Vj: " + Vj + "   Vk " + Vk + "   Qj: " + Qj + "   Qk: " + Qk + "   Busy: " + busy +" num: "+num;
	}
	
	
	public String toString2() {
		return "Address: " + address + "  Busy: " + busy +" num: "+num ;
	}
	public String toString3() {
		return "Address: " + address + " Vj: " + Vj + "   Qj: " + Qj +"  Busy: " + busy +" num: "+num ;
	}
	

}
class register{
	
	String Q="0";
	double value=0;
}




