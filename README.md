# foreman-bulk-configure

## Status

[![Build Status](https://travis-ci.com/delawr0190/foreman-bulk-configure.svg?branch=main)](https://travis-ci.com/delawr0190/foreman-bulk-configure)

### Bulk Configure

Bulk Configure is an open-source Java application that leverages the Foreman API
to streamline the miner on-boarding process.

Depending on the configuration provided, on a per-miner basis, it can:

1. Assign a static IP address and custom hostname
2. Assign a custom miner name in Foreman
3. Assign an initial set of pools and worker names
4. Assign tags to the miner in Foreman
5. Automatically update the Foreman Site Map with a rack and location
6. Tag the miner in Foreman with the provided serial number

#### How to run

1. Obtain client ID and API key
   from [here](https://dashboard.foreman.mn/dashboard/profile/).
2. Obtain Pickaxe ID
   from [here](https://dashboard.foreman.mn/dashboard/pickaxe/).

##### Linux

```sh
./bin/bulk-configure.sh --client.id=<client_id> --client.pickaxe=<pickaxe> --user.apiKey=<api_key>
```

##### Windows

```
bin\bulk-configure.bat --client.id=<client_id> --client.pickaxe=<pickaxe> --user.apiKey=<api_key>
```

#### How to configure

The configuration for all miners that are to be configured must be provided in a
file in the ```conf``` directory, and the file must be called
```on-boarding.csv```.

##### Static IP and Custom Hostname

To assign a static IP, the following columns must be non-empty in the
`on-boarding.csv` file:

- static_ip
- netmask
- gateway
- dns

To assign a hostname, the columns above must be provided, in addition to the
`hostname` column.

##### Custom miner name

To assign a custom miner name, the ```name``` column must be non-empty. The name
value can leverage variable patterns as available on the Foreman dashboard.
Those are currently:

- ```${miner.name}```
- ```${miner.ip}```
- ```${miner.ip.1}```
- ```${miner.ip.2}```
- ```${miner.ip.3}```
- ```${miner.ip.4}```
- ```${miner.mac}```
- ```${miner.mac:clean}```
- ```${miner.port}```
- ```${miner.type}```
- ```${miner.mrr_rig_id}```
- ```${pickaxe}```

##### Initial pools

To assign an initial pool configuration, the following columns must be
non-empty:

- pool_1_url
- pool_1_user
- pool_1_pass
- pool_2_url
- pool_2_user
- pool_2_pass
- pool_3_url
- pool_3_user
- pool_3_pass

##### Tagging

To assign tags to a miner in Foreman for logical grouping, the following column
must be non-empty:

- tags

To assign more than 1 tag, the tags must be comma separated. Example:

`my_tag_1,new_tag_2,other_tag`

##### Site Map positioning

To assign a Site Map location, the following columns must be non-empty:

- rack
- row
- index

*The rack, by name, must already exist in the Site Map.*

##### Serial Number Tagging

To tag the miner in Foreman with the serial number, the ```serial``` column must
be non-empty.

## License

Copyright © 2021, [OBM LLC](https://obm.mn/). Released under
the [GPL-3.0 License](LICENSE).
