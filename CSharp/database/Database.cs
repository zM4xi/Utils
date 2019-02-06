using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Security;
using System.Text;
using System.Threading.Tasks;

namespace Utils
{
    class Database
    {

        private SqlConnection connection;

        public Database(string host, string database, string username, SecureString password)
        {
            var connectionString = "Server={host};Database={database};";
            var sb = new StringBuilder(connectionString);
            sb.Replace("{host}", host);
            sb.Replace("{database}", database);
            SqlCredential credentials = new SqlCredential(username, password);
            connection = new SqlConnection(sb.ToString(), credentials);

            connection.Open();
        }

        public Database(string host, string database)
        {
            var connectionString = "Server={host};Database={database};Trusted_Connection=True";
            var sb = new StringBuilder(connectionString);
            sb.Replace("{host}", host);
            sb.Replace("{database}", database);
            connection = new SqlConnection(sb.ToString());

            connection.Open();
        }

        public DataTable Query(String sql)
        {
            SqlCommand sqlCommand = new SqlCommand(sql);
            SqlDataAdapter adapter = new SqlDataAdapter();
            DataTable dataTable = new DataTable();
            adapter.SelectCommand = sqlCommand;
            adapter.Fill(dataTable);
            return dataTable;
        }

        public void Use(string database)
        {
            connection.ChangeDatabase(database);
        }

        public int Execute(string sql)
        {
            SqlCommand sqlCommand = new SqlCommand(sql);
            return sqlCommand.ExecuteNonQuery();
        }

        public void CloseConnection()
        {
            connection.Close();
        }

    }
}
