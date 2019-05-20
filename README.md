# ATDP-Simulator

This repository contains an implementation for ATDP-Simulator, a software to generate event logs out of ATDP specifications

## Install

A pre-compiled jar file is available in the `./bin` directory. Otherwise, refer to manual build instructions.

### Manually building

In order to build this project, the `leiningen` and `maven` build tools are required. First, install the local `atdplib-model` jar with:

```
sh install-local-jars.sh
```

Once installed, the command `lein uberjar` will generate an executable jar file with all necessary dependencies in the `./target/uberjar` folder.

## Usage

The ATDP-Simulator can be used as a standalone command-line tool:

```
java -jar atdp-simulator.jar --input INPUT --output OUTPUT --size SIZE
```

Where:

- `--input` Is the path to an input file (examples can be found in `./data/examples`).
- `--output` Is the path of the desired output file. Will overwrite existing files.
- `--size` Is the log size to be generated.


