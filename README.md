# HackerFlow

##Overview
Android application to help speed up Hackathon sign in process using Estimote Beacons. The app allows users to detect Estimote Beacons, and connect to them, redirecting them to an in-app registration form.
Using beacons, hundreds of students can register at once, instead of waiting in endless lines. 

The app was built during WildHacks Fall'15 with the help of Abhishek Verma and Shivein Goyal. We ended up in the top 10! 

##Working
The application connects to an Azure database that Hackathon organizers can manage. Data from the in- app registration form will be pushed in the database. 
Each student, post registration, will get a confirmation SMS through Twilio. Besides the SMS confirmation, the app will generate a unique QR code for each student.
The QR code can be used as an student-ID for the hackathon by the organizers. HackerCheckIn (another app as a repository), would allow organizers to read the QR code, and 
retrieve student data.
