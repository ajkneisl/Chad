public class RussianRoulette implements Command
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args)
    {
        return () -> {
            // TODO calculate user
        };
    }
    
    @Override
    public Runnable help(MessageReceivedEvent e, List<String> args)
    {
        return () -> {
          // TODO here
        };
    }
}
