/*
Copyright (C) 2018-2019 Andres Castellanos

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>
*/

package vsim.utils;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import vsim.Flags;
import vsim.Globals;
import vsim.Logger;
import vsim.VSim;


/** Command line parser utility. */
public final class Cmd {

  /** if title has been printed */
  private static boolean PRINTED = false;

  /**
   * Parses command line arguments.
   *
   * @param args Command line arguments
   * @return A list of paths representing the assembly files (*.s or *.asm) passed.
   */
  public static ArrayList<File> parse(String[] args) {
    // add options
    Options options = new Options();
    options.addOption(Option.builder("h").longOpt("help").build());
    options.addOption(Option.builder("v").longOpt("version").build());
    options.addOption(Option.builder("l").longOpt("license").build());
    options.addOption(Option.builder("b").longOpt("bare").build());
    options.addOption(Option.builder("s").longOpt("self").build());
    options.addOption(Option.builder("e").longOpt("extrict").build());
    options.addOption(Option.builder().longOpt("start").hasArg().build());
    options.addOption(Option.builder().longOpt("hist").hasArg().build());
    options.addOption(Option.builder("g").longOpt("debug").build());
    options.addOption(Option.builder("d").longOpt("dump").hasArg().build());
    options.addOption(Option.builder().longOpt("format").hasArg().build());
    ArrayList<File> files = new ArrayList<>();
    try {
      // parse args
      DefaultParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);
      // set flags
      Flags.HELP = cmd.hasOption("help");
      Flags.VERSION = cmd.hasOption("version");
      Flags.LICENSE = cmd.hasOption("license");
      Flags.BARE = cmd.hasOption("bare");
      Flags.SELF_MODIFYING = cmd.hasOption("self");
      Flags.EXTRICT = cmd.hasOption("extrict");
      if (cmd.hasOption("start")) {
        Flags.START = cmd.getOptionValue("start");
      }
      if (cmd.hasOption("hist")) {
        try {
          Flags.HIST_SIZE = Integer.parseInt(cmd.getOptionValue("hist"));
        } catch (NumberFormatException e) {
          title(true, false);
          Logger.error("invalid history size: " + cmd.getOptionValue("hist"));
          VSim.exit(1);
        }
      }
      if (cmd.hasOption("dump")) {
        Flags.DUMP = new File(cmd.getOptionValue("dump"));
      }
      if (cmd.hasOption("format")) {
        String format = cmd.getOptionValue("format");
        if (format.equals("hex") || format.equals("bin")) {
          Flags.DUMP_FORMAT = format;
        } else {
          title(true, false);
          Logger.error("invalid dump format: " + format);
          VSim.exit(1);
        }
      }
      // set files
      for (String arg : cmd.getArgs()) {
        File file = new File(arg);
        if (!file.exists()) {
          title(true, false);
          Logger.error("'" + file + "' does not exists");
          VSim.exit(1);
        } else if (file.isDirectory()) {
          for (File f : FS.ls(file)) {
            files.add(f);
          }
        } else if (FS.isAssemblyFile(file)) {
          files.add(file);
        }
      }
      // print help message
      help(false);
    } catch (ParseException e) {
      title(true, false);
      Logger.error(e.getMessage().toLowerCase() + Data.EOL);
      help(true);
    } finally {
      files.trimToSize();
      title(false, files.size() == 0);
      return files;
    }
  }

  /**
   * Prints V-Sim title.
   *
   * @param force if true force print
   */
  private static void title(boolean force, boolean exit) {
    if (!PRINTED && (force || Flags.VERSION || Flags.LICENSE)) {
      IO.stdout().println("        _   __    _____");
      IO.stdout().println("       | | / /___/ __(_)_ _");
      IO.stdout().println("       | |/ /___/\\ \\/ /  ' \\");
      IO.stdout().println("       |___/   /___/_/_/_/_/");
      IO.stdout().println();
      IO.stdout().println("RISC-V Assembler & Runtime Simulator");
      // print version ?
      if (Flags.VERSION) {
        IO.stdout().println("              " + Globals.VERSION);
      }
      // print license ?
      if (Flags.LICENSE) {
        IO.stdout().println();
        IO.stdout().println("GPL-3.0 License");
        IO.stdout().println(Globals.LICENSE);
        IO.stdout().println("All Rights Reserved");
        IO.stdout().println("See https://git.io/fpcYS for a full copyright notice");
      }
      // exit simulator
      if (exit) {
        VSim.exit(0);
      } else {
        IO.stdout().println();
      }
      PRINTED = true;
    }
  }

  /**
   * Prints V-Sim help message and exit.
   *
   * @param force if true force print
   */
  private static void help(boolean force) {
    if (force || Flags.HELP) {
      title(true, false);
      IO.stdout().println("usage: vsim [options] <files>");
      IO.stdout().println(Data.EOL + "[General Options]");
      IO.stdout().println("  -h, --help             show V-Sim help message");
      IO.stdout().println("  -v, --version          show V-Sim version");
      IO.stdout().println("  -l, --license          show V-Sim license");
      IO.stdout().println(Data.EOL + "[Simulator Options]");
      IO.stdout().println("  -b, --bare             bare machine (no pseudo-ops)");
      IO.stdout().println("  -s, --self             enable self-modifying code");
      IO.stdout().println("  -e, --extrict          assembler warnings are consider errors");
      IO.stdout().println("  -g, --debug            start debugger");
      IO.stdout().println("      --start  <label>   set global start label (default: __start)");
      IO.stdout().println("      --hist   <size>    maximum number of backstep operations that can be taken");
      IO.stdout().println(Data.EOL + "[Dump Options]");
      IO.stdout().println("  -d, --dump   <file>    dump generated machine code to a file");
      IO.stdout().println("      --format <format>  dump format (hex|bin) (default: hex)");
      IO.stdout().println();
      IO.stdout().println("Please report issues at https://github.com/andrescv/V-Sim/issues");
      // exit simulator
      if (Flags.HELP) {
        VSim.exit(0);
      } else {
        VSim.exit(1);
      }
    }
  }

}
