package exos18sols;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.peg.Normalize;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Randomize;
import exos18.Counter;
import exos18.FirstDigit;

public class Benford
{

  public static void main(String[] args)
  {
    Processor feed = null;
    /*{
      // Source of random numbers
      QueueSource dummy = new QueueSource().setEvents(0);
      feed = new Randomize(1, 99);
      Connector.connect(dummy, feed);
    }*/
    {
      // The first squares
      QueueSource dummy = new QueueSource().setEvents(2);
      Fork f = new Fork(2);
      Connector.connect(dummy, f);
      Counter cnt = new Counter();
      Connector.connect(f, 0, cnt, 0);
      feed = new ApplyFunction(Numbers.power);
      Connector.connect(cnt, 0, feed, 0);
      Connector.connect(f, 1, feed, 1);
    }
    Slice distro = new Slice(FirstDigit.instance, new Counter(1));
    Connector.connect(feed, distro);
    ApplyFunction normalize = new ApplyFunction(Normalize.instance);
    Connector.connect(distro, normalize);
    Pullable p = normalize.getPullableOutput();
    for (int i = 0; i < 1000; i++)
    {
      System.out.println(p.pull());
    }
  }

}
