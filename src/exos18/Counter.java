package exos18;

import ca.uqac.lif.cep.UniformProcessor;

public class Counter extends UniformProcessor
{
  int m_count = 0;
  
  public Counter(int start_value)
  {
    super(1, 1);
    m_count = start_value;
  }
  
  public Counter()
  {
    this(0);
  }

  @Override
  protected boolean compute(Object[] inputs, Object[] outputs)
  {
    outputs[0] = m_count++;
    return true;
  }

  @Override
  public Counter duplicate(boolean with_state)
  {
    Counter c = new Counter();
    if (with_state)
    {
      c.m_count = m_count;
    }
    return c;
  }
}
