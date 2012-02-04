package com.mapr.stats;


import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.Random;

/**
 * Follow a Metropolis random walk that should converge to a beta distribution.  Since the point is
 * to have slowly changing probabilities for simulating non-stationary conversion processes, having
 * substantial sample to sample correlation is good here.
 * <p/>
 * The probabilities returned will be beta distributed if you take enough steps.  Steps are proposed
 * using a normally distributed step in soft-max space which gives a random walk bounded to (0,1) in
 * probability space.  The proposal distribution winds up taking very small steps near the
 * boundaries with larger steps in the middle.  Steps are accepted or rejected according to the
 * Metropolis algorithm.  Computing the probabilities for acceptance or rejection in the probability
 * space while taking the step in log-odds space is OK since the proposal probability is still
 * symmetrical.
 */
public class BetaWalk {
  private Random rand = new MersenneTwisterRNG();

  private double stepSize;
  private BetaDistribution bd;

  double x;
  double pdf = 0;

  public BetaWalk(double alpha, double beta, double stepSize) {
    this.bd = new BetaDistribution(alpha, beta, rand);
    this.stepSize = stepSize;
    x = bd.nextDouble();
    if (x < 0) {
      System.out.printf("heh?\n");
    }
    pdf = bd.pdf(x);
  }

  public double step() {
    double x1 = x + rand.nextGaussian() * stepSize;
    double pdf1 = bd.pdf(x1);
    if (x1 < 0 || x1 > 1) {
      pdf1 = 0;
    }

    if (pdf1 > 0 && (pdf1 > pdf || rand.nextDouble() < pdf1 / pdf)) {
      if (x1 < 0) {
        System.out.printf("huh?\n");
      }

      x = x1;
      pdf = pdf1;
    }
    return x;
  }
}