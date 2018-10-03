package com.jhobot.handle;

public class Permission 
{
    public enum PermGroups
    {
        MUTED, DEFAULT, HELPER, MODERATOR, ADMINISTRATOR, SERVER_ADMINISTRATOR
    }
    
    public enum Permissions
    {
        // perms
    }
    
    public static List<CommandClass> parsePermissionGroupsToCommands(Permset p, DB db)
    {
        
    }
    
    public static boolean userAllowedToCommands(CommandClass, DB db, IUser u)
    {
        
    }
    
    public static void removePermissionFromPermissionGroup(Permset p, DB db, Permissions pr)
    {
          
    }
    
    public static void addPermissionToPermissionGroup(Permset p, DB db, Permisisons pr)
    {
        
    }
    
    public static void addPermissionGroupToRole(Permissions p, DB db, IRole r)
    {
          
    }
}
