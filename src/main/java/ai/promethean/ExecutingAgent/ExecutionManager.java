package ai.promethean.ExecutingAgent;

import ai.promethean.DataModel.*;
import ai.promethean.Logger.Logger;
import ai.promethean.Planner.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ExecutionManager {
    private String className =  this.getClass().getSimpleName();
    /**
     * Take a plan and parsed objects from parser and run the simulation, replanning if necessary
     * @param plan planblock generated by planner
     * @param planObjects list of objects generated by paser
     */
    public List<SystemState> runPlanClock(Plan plan, Map<String, Object> planObjects, int stopTime, boolean activateCLF){
        boolean planCompleted = false;
        //Pull out the goalState, taskDict, and optimizations in case we need to re-plan
        GoalState goalState = (GoalState) planObjects.get("goalState");
        TaskDictionary taskDict = (TaskDictionary) planObjects.get("tasks");
        StaticOptimizations optimizations = (StaticOptimizations) planObjects.get("optimizations");

        Logger.writeLog("Initial State: \n" + plan.getInitialState(), this.className);
        Logger.writeLog("Runtime Goal State:: \n" + plan.getEndState(), this.className);
        Logger.writeLog("Plan: \n" + plan.getPlanBlockList(), this.className);

        Stack<SystemState> stateList= new Stack<SystemState>();
        while (!planCompleted){
            ClockObserver.addState(plan.getInitialState());
            Clock clock = new Clock(plan.getInitialState().getTime());
            ClockObserver tasks = new TaskExecutor(plan);
            clock.addObserver(tasks);
            //check size of plan objects to see if a perturbation exists
            if (planObjects.get("perturbations")!=null) {
                ClockObserver perturbations = new PerturbationInjector((List<Perturbation>)planObjects.get("perturbations"));
                clock.addObserver(perturbations);
            }
            clock.runClock();

            planCompleted = ((TaskExecutor)tasks).isPlanCompleted();
            if(planCompleted){
                Logger.writeLog("Plan Completed \n", "ExecutionManager");
                Logger.writeLog("Ending State: \n" + ClockObserver.peekLastState(), "ExecutionManager");

            }
            // a perturbation has occurred and needs to be handled.
            else{

                Logger.writeLog("Replanning", "ExecutionManager");

                //get the current state of the craft for replanning
                SystemState currentState = ClockObserver.peekLastState();
                plan = generatePlanFromSystemState(currentState, goalState, taskDict, optimizations, stopTime, activateCLF);
                if(plan==null){
                    planCompleted=true;
                }
                else {
                    planCompleted = !plan.getGoalHasBeenReached();
                }


                if ( plan!=null) {
                    Logger.writeLog("New Plan: \n" + plan.getPlanBlockList(), "ExecutionManager");
                }

            }
           stateList=tasks.getStateStack();
        }
        Logger.writeLog("Ending State: \n" + ClockObserver.peekLastState(), "ExecutionManager");
        return stateList;
    }

    /**
     * Generate a plan from current system state if replanning is necessary
     * @param currentState
     * @param goalState
     * @param taskDictionary
     * @param optimizations
     * @return
     */
    public Plan generatePlanFromSystemState(SystemState currentState,
                                            GoalState goalState,
                                            TaskDictionary taskDictionary,
                                            StaticOptimizations optimizations,
                                            int stopTime,
                                            boolean activateCLF){
        Algorithm algo = new AStar();
        Planner planner = new Planner(algo);
        Plan plan = planner.plan(currentState, goalState, taskDictionary, optimizations, stopTime, activateCLF);
        return plan;
    }
}
