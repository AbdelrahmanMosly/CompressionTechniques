require 'csv'

# Your user data
user_data = []


# Write the data to the CSV file
CSV.open("/home/nowlun/public/contacts.csv", 'w') do |csv|
  # Write headers if needed
  csv << ['Name', 'Phone']

  # Write each row of user data
  User.all.each do |user|
    csv << [Phonelib.parse(user.phone).full_e164, user.phone]
  end
end


