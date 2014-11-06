package com.forgeessentials.core.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.permissions.PermissionContext;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;
import net.minecraftforge.server.CommandHandlerForge;

import com.forgeessentials.api.permissions.FEPermissions;

public abstract class ForgeEssentialsCommandBase extends CommandBase {

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (sender instanceof EntityPlayer)
        {
            processCommandPlayer((EntityPlayer) sender, args);
        }
        else if (sender instanceof TileEntityCommandBlock)
        {
            processCommandBlock((CommandBlockLogic) sender, args);
        }
        else
        {
            processCommandConsole(sender, args);
        }
    }

    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        throw new CommandException(String.format("Command %s is not implemented for players", getCommandName()));
    }

    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        throw new CommandException(String.format("Command %s is not implemented for console", getCommandName()));
    }

    public void processCommandBlock(CommandBlockLogic block, String[] args)
    {
        processCommandConsole(block, args);
    }

    // ------------------------------------------------------------
    // Command usage

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            if (canPlayerUseCommand((EntityPlayer) sender))
                return canCommandSenderUseCommandException(FEPermissions.MSG_NO_PLAYER_COMMAND);
        }
        else if (sender instanceof TileEntityCommandBlock)
        {
            if (canCommandBlockUseCommand((TileEntityCommandBlock) sender))
                return canCommandSenderUseCommandException(FEPermissions.MSG_NO_BLOCK_COMMAND);
        }
        else
        {
            if (!canConsoleUseCommand())
                return canCommandSenderUseCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
        }

        // Check permission
        if (!checkCommandPermission(sender))
            return false;

        return true;
    }

    protected static boolean canCommandSenderUseCommandException(String msg)
    {
        // Find out if, if canCommandSenderUseCommand was called from within executeCommand method of CommandHandler.
        // Only if it's called from there, it's safe to throw an exception.
        final String className = CommandHandler.class.getName();
        final String methodName = "executeCommand";
        for (StackTraceElement s : Thread.currentThread().getStackTrace())
            if (s.getClassName().equals(className))
            {
                if (s.getClassName().equals(methodName))
                    throw new CommandException(msg);
                break;
            }
        // Just return false instead of an exception
        return false;
    }

    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        return true;
    }

    public abstract boolean canConsoleUseCommand();

    public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
    {
        return canConsoleUseCommand();
    }

    // ------------------------------------------------------------
    // Permissions

    /**
     * Registers this command and it's permission node
     */
    public void register()
    {
        CommandHandlerForge.registerCommand(this, getPermissionNode(), getDefaultPermission());
    }

    /**
     * Get the permission node
     */
    public abstract String getPermissionNode();

    /**
     * Check, if the sender has permissions to use this command
     */
    public boolean checkCommandPermission(ICommandSender sender)
    {
        return PermissionsManager.checkPermission(new PermissionContext().setCommandSender(sender).setCommand(this), getPermissionNode());
    }

    /**
     * Get the default permission level needed to use this command
     */
    public abstract RegisteredPermValue getDefaultPermission();

    // ------------------------------------------------------------
    // Utilities

    /**
     * Simply prints a usage message to the sender of the command.
     */
    public void error(ICommandSender sender)
    {
        error(getCommandUsage(sender));
    }

    /**
     * Prints an error message to the sender of the command.
     */
    public void error(String message)
    {
        throw new CommandException(message);
    }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof ICommand)
            return this.compareTo((ICommand) o);
        return 0;
    }

    /**
     * Parse int with support for relative int.
     *
     * @param sender
     * @param string
     * @param relativeStart
     * @return
     */
    public static int parseInt(ICommandSender sender, String string, int relativeStart)
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(sender, string);
        }
        else
        {
            return parseInt(sender, string);
        }
    }

    /**
     * Parse double with support for relative values.
     *
     * @param sender
     * @param string
     * @param relativeStart
     * @return
     */
    public static double parseDouble(ICommandSender sender, String string, double relativeStart)
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(sender, string);
        }
        else
        {
            return parseInt(sender, string);
        }
    }

}
