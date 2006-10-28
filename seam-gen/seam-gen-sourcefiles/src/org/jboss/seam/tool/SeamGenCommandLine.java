package org.jboss.seam.tool;

public class SeamGenCommandLine {

	public static void main(String[] args) throws Exception {

		if (args[0].equals("set-properties")) {
			// check to make sure we have right # of arguments
			if (args.length == 9) {
				BuildPropertiesBean projectProps = new BuildPropertiesBean(args);
				BuildPropertiesGenerator propsGen = new BuildPropertiesGenerator(
						projectProps);
				propsGen.generate();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		} else if (args[0].equals("new-action")) {
			if (args.length == 3) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
				actionGen.newAction();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}

		else if (args[0].equals("new-stateless-action")) {
			if (args.length == 3) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
				actionGen.newStatelessAction();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}

		else if (args[0].equals("new-conversation")) {
			if (args.length == 3) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
				actionGen.newConversation();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}

		else if (args[0].equals("new-testcase")) {
			if (args.length == 3) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
				actionGen.newTestcase();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}

		else if (args[0].equals("new-bpm-action")) {
			if (args.length == 3) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
				actionGen.newBpmAction();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}

		else if (args[0].equals("new-entity")) {
			if (args.length == 3) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
				actionGen.newEntity();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}

		else if (args[0].equals("new-mdb")) {
			if (args.length == 5) {
				JavaClassGenerator actionGen = new JavaClassGenerator(args);
				actionGen.setMdbDestination(args[3]);
				actionGen.setMdbDestinationType(args[4]);
				actionGen.newMdb();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		} 
		
		else if (args[0].equals("new-page")) {
			if (args.length == 3) {
				FaceletGenerator faceletGen = new FaceletGenerator(args);
				faceletGen.newPage();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}
		
		else if (args[0].equals("new-action-page")) {
			if (args.length == 4) {
				FaceletGenerator faceletGen = new FaceletGenerator(args);
				faceletGen.setActionName(args[3]);
				faceletGen.newActionPage();
			} else {
				throw new Exception("Wrong number of arguments");
			}
		}
		
		else {
			System.out.println("No command executed");
		}
	}

}
