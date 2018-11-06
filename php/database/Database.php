<?php

include_once 'system/config/config.php';
include_once 'system/config/database.php';

class Database
{

    protected $connection;

    public function __construct()
    {
        try {
            $this->connection = new PDO(DB['CONNECTOR'] . ':host=' . DB['HOST'] . ';dbname=' . DB['DATABASE'] . ';port=' . DB['PORT'] . ';charset=UTF8', DB['USER'], DB['PASSWORD']);
            $this->connection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        } catch (PDOException $ex) {
            echo '<div class="alert alert-warning alert-dismissible fade show" role="alert">' .
                '<h4 class="alert-heading">Error</h4>' .
                '<p>It seems we run into a little trouble. Could you help us by sending us following error code from below so we can determine the reason for the error. \(°Ω°)/</p>' .
                '<hr>' .
                '<p class="mb-0">' . $ex->getMessage() . '</p>' .
                '</div>';
        }
    }

    public function query(&$sql, $parameters = [])
    {
        try {
            $stmt = $this->connection->prepare($sql);
            $index = 1;
            if (!empty($parameters)) {
                foreach ($parameters as $parameter) {
                    $index++;
                    $stmt->bindParam($index, $parameter);
                }
            }
            $stmt->execute();
            $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
            return $stmt->fetchAll();

        } catch (PDOException $ex) {
            $error = '<div class="alert alert-warning alert-dismissible fade show" role="alert">' .
                '<h4 class="alert-heading">Error</h4>' .
                '<p>It seems we run into a little trouble. Could you help us by sending us following error code from below so we can determine the reason for the error. \(°Ω°)/</p>' .
                '<hr>' .
                '<p class="mb-0">' . $ex->getMessage() . '</p>';
            if (Config['DEBUG']) {
                $error = $error . '<p class="mb-0">' . $sql . '</p>';
            }
            $error = $error . '</div>';
            echo $error;
        }
    }

    public function update(&$sql, $parameters = [])
    {
        try {
            $stmt = $this->connection->prepare($sql);
            $index = 1;
            if (!empty($parameters)) {
                foreach ($parameters as $parameter) {
                    $index++;
                    $stmt->bindParam($index, $parameter);
                }
            }
            $stmt->execute();
        } catch (PDOException $ex) {
            $error = '<div class="alert alert-warning alert-dismissible fade show" role="alert">' .
                '<h4 class="alert-heading">Error</h4>' .
                '<p>It seems we run into a little trouble. Could you help us by sending us following error code from below so we can determine the reason for the error. \(°Ω°)/</p>' .
                '<hr>' .
                '<p class="mb-0">' . $ex->getMessage() . '</p>';
            if (Config['DEBUG']) {
                $error = $error . '<p class="mb-0">' . $sql . '</p>';
            }
            $error = $error . '</div>';
            echo $error;
        }
    }

    public function execute(&$sql)
    {
        try {
            $this->connection->exec($sql);
        } catch (PDOException $ex) {
            $error = '<div class="alert alert-warning alert-dismissible fade show" role="alert">' .
                '<h4 class="alert-heading">Error</h4>' .
                '<p>It seems we run into a little trouble. Could you help us by sending us following error code from below so we can determine the reason for the error. \(°Ω°)/</p>' .
                '<hr>' .
                '<p class="mb-0">' . $ex->getMessage() . '</p>';
            if (Config['DEBUG']) {
                $error = $error . '<p class="mb-0">' . $sql . '</p>';
            }
            $error = $error . '</div>';
            echo $error;
        }
    }

    public function insert(&$sql, $parameters = [])
    {
        try {
            $stmt = $this->connection->prepare($sql);
            $index = 1;
            if (!empty($parameters)) {
                foreach ($parameters as $parameter) {
                    $index++;
                    $stmt->bindParam($index, $parameter);
                }
            }
            $stmt->execute();
        } catch (PDOException $ex) {
            $error = '<div class="alert alert-warning alert-dismissible fade show" role="alert">' .
                '<h4 class="alert-heading">Error</h4>' .
                '<p>It seems we run into a little trouble. Could you help us by sending us following error code from below so we can determine the reason for the error. \(°Ω°)/</p>' .
                '<hr>' .
                '<p class="mb-0">' . $ex->getMessage() . '</p>';
            if (Config['DEBUG']) {
                $error = $error . '<p class="mb-0">' . $sql . '</p>';
            }
            $error = $error . '</div>';
            echo $error;
        }
    }

    public function createTable($table, $parameters = [])
    {
        try {
            $sql = 'CREATE TABLE ' .
                DB['PRAEFIX'] . $table .
                ' (' .
                implode(", ", $parameters) .
                ')';
            $this->connection->exec($sql);
        } catch (PDOException $ex) {
            $error = '<div class="alert alert-warning alert-dismissible fade show" role="alert">' .
                '<h4 class="alert-heading">Error</h4>' .
                '<p>It seems we run into a little trouble. Could you help us by sending us following error code from below so we can determine the reason for the error. \(°Ω°)/</p>' .
                '<hr>' .
                '<p class="mb-0">' . $ex->getMessage() . '</p>';
            if (Config['DEBUG']) {
                $error = $error . '<p class="mb-0">' . $sql . '</p>';
            }
            $error = $error . '</div>';
            echo $error;
        }
    }

    public function close()
    {
        $this->connection = null;
    }

}