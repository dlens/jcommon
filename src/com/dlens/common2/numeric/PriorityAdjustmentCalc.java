package com.dlens.common2.numeric;
public class PriorityAdjustmentCalc {
    
    private double[] costarray;
    private double[] priorityarray;
    private double costdistance;
    private double prioritydistance;

    public PriorityAdjustmentCalc(double[] costs, double[] priorities) {
           costarray = costs;
           priorityarray = priorities;
    }
   
    private double findmin(double[] arrayofstuff) {
           double answer = Double.POSITIVE_INFINITY;
           int size = arrayofstuff.length;
           for (int i = 0; i < size; i++) {
                  if (arrayofstuff[i] < answer) answer = arrayofstuff[i];
           }
           return answer;
    }
   
    private double findmax(double[] arrayofstuff) {
           double answer = Double.NEGATIVE_INFINITY;
           int size = arrayofstuff.length;
           for (int i = 0; i < size; i++) {
                  if (arrayofstuff[i] > answer) answer = arrayofstuff[i];
           }
           return answer;
    }
   
    public double[] transformation() {
           if (priorityarray.length != costarray.length) throw new UnsupportedOperationException("Priority and cost arrays must be same length.");
           double mincost = findmin(costarray);
           double maxcost = findmax(costarray);
           double minpriority = findmin(priorityarray);
           double maxpriority = findmax(priorityarray);
           costdistance = maxcost/mincost;
           if (costdistance < 10) return priorityarray.clone();
           prioritydistance = maxpriority/minpriority;
           double[] logcosts = new double[costarray.length];
           double logCostsConstant = 0;
           if (mincost <= 1)
        	   logCostsConstant = -Math.log10(mincost)+1;
           for (int i = 0; i < costarray.length; i++) {
                  logcosts[i] = Math.log10(costarray[i]) + logCostsConstant;
           }
           double logdistance;
           if (Math.log10(mincost) > 0) {
        	   logdistance = Math.log10(maxcost)/Math.log10(mincost);
           } else {
        	   //If min cost is <= 1, we either would divide by zero above,
        	   //or we would take the log of a negative number below.
        	   logdistance = (Math.log10(maxcost) - Math.log10(mincost) + 1) / (Math.log10(mincost) - Math.log10(mincost) + 1);
           }
           double logadjustment = Math.log10(prioritydistance)/Math.log10(logdistance);
           double[] logadjustedcosts = new double[costarray.length];
           for (int i = 0; i < costarray.length; i++) {
                  logadjustedcosts[i] = Math.pow(logcosts[i], logadjustment);
           }
           double[] magnitudedifference = new double[costarray.length];
           double[] priorityunadjusted = new double[costarray.length];
           double[] priorityadjusted = new double[costarray.length];
           for (int i = 0; i < costarray.length; i++) {
                  magnitudedifference[i] = costarray[i]/logadjustedcosts[i];
                  priorityunadjusted[i] = priorityarray[i]*magnitudedifference[i];
           }
           double maxunadjusted = findmax(priorityunadjusted);
           for (int i = 0; i < costarray.length; i++) {
                  priorityadjusted[i] = priorityunadjusted[i]/maxunadjusted;
           }
           return priorityadjusted;
    }

    public double[] transformCosts() {
        if (priorityarray.length != costarray.length) throw new UnsupportedOperationException("Priority and cost arrays must be same length.");
        double mincost = findmin(costarray);
        double maxcost = findmax(costarray);
        double minpriority = findmin(priorityarray);
        double maxpriority = findmax(priorityarray);
        costdistance = maxcost/mincost;
        if (costdistance < 10) return priorityarray.clone();
        prioritydistance = maxpriority/minpriority;
        double[] logcosts = new double[costarray.length];
        double logCostsConstant = 0;
        if (mincost <= 1)
     	   logCostsConstant = -Math.log10(mincost)+1;
        for (int i = 0; i < costarray.length; i++) {
               logcosts[i] = Math.log10(costarray[i]) + logCostsConstant;
        }
        double logdistance;
        if (Math.log10(mincost) > 0) {
     	   logdistance = Math.log10(maxcost)/Math.log10(mincost);
        } else {
     	   //If min cost is <= 1, we either would divide by zero above,
     	   //or we would take the log of a negative number below.
     	   logdistance = (Math.log10(maxcost) - Math.log10(mincost) + 1) / (Math.log10(mincost) - Math.log10(mincost) + 1);
        }
        double logadjustment = Math.log10(prioritydistance)/Math.log10(logdistance);
        double[] logadjustedcosts = new double[costarray.length];
        for (int i = 0; i < costarray.length; i++) {
               logadjustedcosts[i] = Math.pow(logcosts[i], logadjustment);
        }
        return logadjustedcosts;
    }
}
