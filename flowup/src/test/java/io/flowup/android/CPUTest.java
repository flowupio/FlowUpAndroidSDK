/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import io.flowup.unix.Terminal;
import io.flowup.utils.TestResourcesFileReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class) public class CPUTest {

  private static final int ANY_PID = 21339;

  @Mock private Terminal terminal;
  @Mock private App app;

  private CPU cpu;

  @Before public void setUp() {
    cpu = new CPU(app, terminal);
  }

  @Test public void returnsTheCPUUsageIfThereIsNoProblem() throws Exception {
    givenTheAppPIDIs(ANY_PID);
    givenTheTerminalReturnsAnExpectedOutput();

    int usage = cpu.getUsage();

    assertEquals(8, usage);
  }

  @Test public void returnsZeroIfTheTerminalOutputIsNotCorrect() throws Exception {
    givenTheAppPIDIs(ANY_PID);
    givenTheTerminalReturnsAnError();

    int usage = cpu.getUsage();

    assertEquals(0, usage);
  }

  @Test public void returnsZeroIfThePIDIsNotInTheTerminalOutput() throws Exception {
    givenTheAppPIDIs(-1);
    givenTheTerminalReturnsAnError();

    int usage = cpu.getUsage();

    assertEquals(0, usage);
  }

  private void givenTheTerminalReturnsAnError() throws Exception {
    String correctTopExecutionOutput =
        TestResourcesFileReader.getContentFromFileWithNewLines("terminal/errorTopExecution.txt");
    when(terminal.exec("top -s cpu -n 1")).thenReturn(correctTopExecutionOutput);
  }

  private void givenTheAppPIDIs(int pid) {
    when(app.getPid()).thenReturn(pid);
  }

  private void givenTheTerminalReturnsAnExpectedOutput() throws Exception {
    String correctTopExecutionOutput =
        TestResourcesFileReader.getContentFromFileWithNewLines("terminal/correctTopExecution.txt");
    when(terminal.exec("top -s cpu -n 1")).thenReturn(correctTopExecutionOutput);
  }
}