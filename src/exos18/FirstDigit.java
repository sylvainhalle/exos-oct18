package exos18;

import ca.uqac.lif.cep.functions.UnaryFunction;

public class FirstDigit extends UnaryFunction<Number,Number>
{
  public static final transient FirstDigit instance = new FirstDigit();
  
  FirstDigit()
  {
    super(Number.class, Number.class);
  }
  
  @Override
  public Number getValue(Number x)
  {
    int y = x.intValue();
    while (y > 9)
    {
      y /= 10;
    }
    return y;
  }

}
