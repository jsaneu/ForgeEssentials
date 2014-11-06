package com.forgeessentials.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;

public class CommandColorize extends FEcmdModuleCommands {

    @Override
    public String getCommandName()
    {
        return "colorize";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        sender.getEntityData().setBoolean("colorize", true);
        OutputHandler.chatConfirmation(sender, "Right click a sign to colourize it!");
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/colorize Apply pre-existing colour codes to a sign.";
    }
}
