/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.progress4j;

import static com.github.utils4j.imp.Throwables.tryRun;

import java.util.List;
import java.util.Random;

import com.github.progress4j.imp.ProgressFactories;
import com.github.utils4j.gui.imp.LookAndFeelsInstaller;
import com.github.utils4j.imp.Threads;

public class TestSample {
  
  static {
    tryRun(() -> LookAndFeelsInstaller.install("Metal"));
  }
  
  enum Stage implements IStage {
    PROCESSING
  }
  
  static IProgressFactory FACTORY = ProgressFactories.THREAD;
  
  static Random RANDOM = new Random();
  
  public static void main(String[] args) throws InterruptedException {

    List<Thread> requests = new java.util.ArrayList<>();
    
    for(int i = 0; i < 1000; i++) {
      Thread t = newRequest(10, 300);
      requests.add(t);
      Thread.sleep(RANDOM.nextInt(5000));
    }

    for(Thread r: requests) {
      r.join();
    }
    
    System.out.println("FIM");
  }

  private static Thread newRequest(int mod, int total) {
    return Threads.startAsync(() -> {
      IProgressView progress = FACTORY.get();
      progress.display();
      try {
        progress.begin(Stage.PROCESSING, total);
        for(int i = 1; i <= total; i++) {
          progress.step("Operação  " + i);
          Threads.sleep(RANDOM.nextInt(100));
        }
        progress.end();
      }catch(Exception e) {
        e.printStackTrace();
      }finally {
        progress.undisplay();
        progress.dispose();
      }
    });
  }
}
