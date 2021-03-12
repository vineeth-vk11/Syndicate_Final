package com.syndicate.SalesHelper.AttendanceHelper;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.syndicate.R;
import com.syndicate.TransactionsHelper.SortTransactionsDialog;
import com.syndicate.TransactionsHelper.TransactionsFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class SalesAttendanceFragment extends Fragment implements SortAttendanceDialog.OnDatesSelected{

    RecyclerView attendance;
    ArrayList<AttendanceModel> attendanceModelArrayList;
    ArrayList<AttendanceModel> attendanceModelArrayList1;
    ArrayList<AttendanceModel> attendanceModelArrayList2;
    FirebaseFirestore db;

    String  company, sales, name, address;

    Button dateSelector;
    ImageButton download;

    Date startDateD, endDateD, dateD;
    String startDateS, endDateS;

    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    ImageView noAttendanceImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_attendance, container, false);

        Bundle bundle = getArguments();

        company = bundle.getString("company");
        sales = bundle.getString("sales");
        name = bundle.getString("name");

        Log.i("name",name);

        attendance = view.findViewById(R.id.attendanceRecycler);
        attendance.setLayoutManager(new LinearLayoutManager(getContext()));
        attendance.setHasFixedSize(true);

        attendanceModelArrayList = new ArrayList<>();
        attendanceModelArrayList1 = new ArrayList<>();
        attendanceModelArrayList2 = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        getAttendance(date);

        dateSelector = view.findViewById(R.id.imageButton4);
        download = view.findViewById(R.id.imageButton5);
        noAttendanceImage = view.findViewById(R.id.noAttendanceImage);

        dateSelector.setText(date);

        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButton();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortAttendanceDialog sortAttendanceDialog = new SortAttendanceDialog();
                sortAttendanceDialog.setTargetFragment(SalesAttendanceFragment.this, 1);
                sortAttendanceDialog.show(getActivity().getSupportFragmentManager(), "Download Attendance Report");
            }
        });
        return view;
    }

    private void getAttendance(final String dateSelected){

        db.collection("Companies").document(company).collection("sales").document(sales).collection("attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                attendanceModelArrayList.clear();
                attendanceModelArrayList1.clear();
                for(DocumentSnapshot documentSnapshot: task.getResult()){

                    AttendanceModel attendanceModel = new AttendanceModel();
                    attendanceModel.setName(documentSnapshot.getString("name"));
                    attendanceModel.setDate(documentSnapshot.getString("date"));
                    attendanceModel.setTime(documentSnapshot.getString("time"));

                    try {
                        attendanceModel.setDateD(new SimpleDateFormat("dd-MM-yyyy").parse(documentSnapshot.getString("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(dateSelected.equals(documentSnapshot.getString("date"))){
                        attendanceModelArrayList.add(attendanceModel);
                    }

                    attendanceModelArrayList1.add(attendanceModel);

                }

                if(attendanceModelArrayList.size() == 0){
                    noAttendanceImage.setVisibility(View.VISIBLE);
                }
                else {
                    noAttendanceImage.setVisibility(View.GONE);
                }
                Log.i("size", String.valueOf(attendanceModelArrayList.size()));
                Log.i("Size1", String.valueOf(attendanceModelArrayList1.size()));

                AttendanceAdapter attendanceAdapter = new AttendanceAdapter(getContext(),attendanceModelArrayList);
                attendance.setAdapter(attendanceAdapter);
            }
        });
    }

    private void handleDateButton(){

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int newMonth = month + 1;

                String dateFinal = "";

                if(dayOfMonth<10 && month <10){
                    dateFinal = "0" + dayOfMonth + "-" +  "0" + newMonth + "-" + year;
                }
                else if(dayOfMonth<10 && month>10){
                    dateFinal = "0" + dayOfMonth + "-" +  newMonth + "-" + year;
                }
                else if(dayOfMonth>10 && month<10){
                    dateFinal = dayOfMonth + "-" +  "0" + newMonth + "-" + year;
                }

                dateSelector.setText(dateFinal);
                getAttendance(dateFinal);
            }
        }, year, month, date);

        datePickerDialog.show();
    }

    @Override
    public void sendInput(String startDate, String endDate) throws IOException {
        Log.i("startDate",startDate);
        Log.i("End Date",endDate);

        startDateS = startDate;
        endDateS = endDate;

        sort(startDate, endDate);
    }

    public void sort(String startDate, String endDate) throws IOException {

        attendanceModelArrayList2.clear();

        try {
            startDateD = new SimpleDateFormat("dd-MM-yyyy").parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            endDateD = new SimpleDateFormat("dd-MM-yyyy").parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        for(int i=0; i<attendanceModelArrayList1.size();i++){

            try {
                dateD = new SimpleDateFormat("dd-MM-yyyy").parse(attendanceModelArrayList1.get(i).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(!dateD.before(startDateD)&& !dateD.after(endDateD)){
                attendanceModelArrayList2.add(attendanceModelArrayList1.get(i));
            }
        }

        Comparator c = Collections.reverseOrder();
        Collections.sort(attendanceModelArrayList2,c);
        Collections.reverse(attendanceModelArrayList2);

        generatePdf();

    }

    public void generatePdf() throws IOException {

        Log.i("Entered","True");

        String path = Environment.getExternalStorageDirectory() + File.separator + "LedgerAttendance.pdf";
        File file = new File(path);

        if(!file.exists()){
            file.createNewFile();
        }

        try{
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();

            BaseFont fontName = BaseFont.createFont("assets/fonts/Roboto-Black.ttf","UTF-8" , BaseFont.EMBEDDED);
            BaseFont font1 = BaseFont.createFont("assets/fonts/Roboto-Regular.ttf","UTF-8" , BaseFont.EMBEDDED);

            Font titleFont = new Font(fontName, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font contactFont = new Font(font1, 12.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerFont = new Font(fontName, 24.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerAddressFont = new Font(font1, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont = new Font(font1, 16.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont1 = new Font(fontName, 16.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont2 = new Font(fontName, 16.0f, Font.NORMAL, BaseColor.WHITE);

            addNewItem(document, "21 ST CENTURY BUSINESS SYNDICATE", Element.ALIGN_CENTER, titleFont);
            addNewItem(document, "Contact : 0612-2325412,9334120345", Element.ALIGN_CENTER, contactFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "Attendance Details",Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));

            addNewItem(document, name,Element.ALIGN_CENTER,dealerAddressFont);
            document.add(new Paragraph(" "));

            addNewItem(document, startDateS +" - " + endDateS,Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            float[] columnWidths = {400f,400f,100f};

            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Paragraph(new Chunk("Dealer", titleFont)));
            cell1.setBorderColor(BaseColor.WHITE);
            cell1.setFixedHeight(50f);

            PdfPCell cell2 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Date", titleFont))));
            cell2.setBorderColor(BaseColor.WHITE);
            cell2.setFixedHeight(50f);

            PdfPCell cell3 = new PdfPCell(new Paragraph( new Paragraph(new Chunk("Time.", titleFont))));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorderColor(BaseColor.WHITE);
            cell3.setFixedHeight(50f);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);

            for(int i = 0; i<attendanceModelArrayList2.size();i++){

                PdfPCell cell6 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(attendanceModelArrayList2.get(i).getName(), transactionFont))));
                PdfPCell cell7 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(attendanceModelArrayList2.get(i).getDate(), transactionFont))));
                PdfPCell cell8 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(attendanceModelArrayList2.get(i).getTime(), transactionFont))));

                cell6.setFixedHeight(30f);
                cell7.setFixedHeight(30f);
                cell8.setFixedHeight(30f);

                cell6.setBorderColor(BaseColor.WHITE);
                cell7.setBorderColor(BaseColor.WHITE);
                cell8.setBorderColor(BaseColor.WHITE);

                table.addCell(new PdfPCell(cell6));
                table.addCell(new PdfPCell(cell7));
                table.addCell(new PdfPCell(cell8));

                table.setSpacingBefore(10f);

            }

            document.add(table);

            document.close();

            Toast.makeText(getContext(),"Pdf generated",Toast.LENGTH_SHORT).show();

        }catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void addNewItem(Document document, String text, int alignCenter, Font font) throws DocumentException {

        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(alignCenter);
        document.add(paragraph);
    }

}