Clone-counter Gerrit plugin
=========================================

Description
-----------
The plugin allows to track the number of clones for one or more Gerrit repositories on a PostreSQL database.

The user can activate the plugin either for all the Gerrit repositories or for just for few of them.
The user can also choose custom DB name and table structures.

Supported Gerrit versions
-------------------------
- Gerrit 2.10 (https://code.google.com/p/gerrit/)

Database requirements
---------------------
The database needs to be a PostgreSQL database, version 9.X.
It's required a table (see below for custom table and columns names) with three columns.
The first column is used to track the clones dates and must be of type `date`.
The second column is used to track the number of clones for a specific repository and it must be of type `integer`.
The third column is used to keep track the name of the repository cloned and it must be of type `varchar`.
It's strongly suggested to use as the primary key the date and the repository fields.

Plugin configuration
--------------------
In order to configure the plugin, the user should create a dedicated subsection in the `gerrit.config` configuration file.
Each time the configuration is modified, either Gerrit need to be restarted or the plugin must be reloaded in order for it
to parse the new configuration.

To start the plugin configuration block create a line in `gerrit.config` like the following:

```
[plugin "clone counter"]
```

Following parameters must be inserted below the specific plugin section in the `gerrit.config`.

- **Timezone.**
  Specify the timezone you want to use to save the entries in the database. The default value
  is GMT.

  ```
  timezone = America/Los_Angeles
  ```

- **Active Trackers.**
  The parameter is mandatory. It allow you to specify what behavior a user want to monitor.
  It's a comma separated values list. For example, if you want to monitor the number of
  clones repositories specify clone, if you want to monitor also the number of fetches and pulls
  specify update. You can also specify both, as in the example below.

  ```
  activeTrackers = clone,update
  ```

- **Active Repos.**
  The user can specifiy a list of repository to be tracked. If no repositories are specified, by default all the repositories will be tracked. Please, provide a comma separated value of repository names. For example,

  ```
  activeRepos = repoOne,repoTwo,repoThree
  ```

- **Database address:**

  ```
  dbUrl = db.example.com
  ```

- **Database port:**

  ```
  dbPort = 5432
  ```

- **Database user:**

  ```
  dbUser = admin
  ```

- **Database password.**

  ```
  dbPass = pass
  ```

- **Application table.**
  The table used in SQL by the application. For example,

  ```
  dbTable = my-db-table
  ```

- **Date field name.**
  Name of the column used in the SQL table to keep track of the dates. For example,

  ```
  dbDateCol = date
  ```

- **Clones Counter DB column name.**
  Name of the column used in the SQL table to keep track of the number of clones. For example,

```
dbClonesCounterCol = clones
```

- **Updates Counter DB column name.**
  Name of the column used in the SQL table to keep track of the number of updates.
  (fetches and pulls). For example,

```
dbUpdatesCounterCol = updates
```

- Repository field name. Name of the column used in the SQL table to keep track of the repository name. For example,

  ```
  dbRepoCol = repo
  ```

Configuration example
---------------------
Below it's reported an example of a typical plugin configuration.
The example uses all the parameters currently supported. Please, refer to the guide above to see what are the default values
and behaviors if a parameter is not specified.

```
[plugin "clone-counter"]
        dbUrl = db.example.com
        dbPort = 5432
        dbUser = admin
        dbPass = mypass
        dbName = clone-db
        dbTable = clone-table
        dbDateCol = date
        dbCounterCol = clones
        dbRepoCol = repo
```

Default values
--------------
Providing a configuration it's not necessarily need. If not parameters are found, by default the application use
the following values/behaviors.

```
[plugin "clone-counter"]
	activeRepos = * (All repositories are tracked)
        dbUrl = 127.0.0.1
        dbPort = 5432
        dbUser = admin
        dbPass = pass
        dbName = default-db
        dbTable = default-table
        dbDateCol = date
        dbCounterCol = clones
        dbRepoCol = repos
```
