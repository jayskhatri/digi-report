package com.digitalpathology.digi_report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.object.MedicalReport;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {

    private List<MedicalReport> reports;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_report, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MedicalReport report = reports.get(position);
        holder.reportName.setText(report.getReportName());
        holder.uploadDate.setText(report.getReportDate());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView uploadDate, reportName;

        public MyViewHolder(View view) {
            super(view);
            uploadDate = view.findViewById(R.id.report_uploaddate);
            reportName = view.findViewById(R.id.report_name);
        }
    }

    public ReportAdapter(List<MedicalReport> medicalReports) {
        this.reports = medicalReports;
    }


}
