/**
 * Copyright (C) 2013 The Simlar Authors.
 *
 * This file is part of Simlar. (https://www.simlar.org)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.simlar.widgets;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.simlar.R;
import org.simlar.helper.ContactDataComplete;
import org.simlar.logging.Lg;
import org.simlar.utils.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

final class ContactsAdapter extends ArrayAdapter<ContactDataComplete>
{
	private final int mLayout;
	private final LayoutInflater mInflater;

	private final class SortByName implements Comparator<ContactDataComplete>
	{
		@Override
		public int compare(final ContactDataComplete lhs, final ContactDataComplete rhs)
		{
			if (lhs == null && rhs == null) {
				return 0;
			}

			if (lhs == null) {
				return -1;
			}

			if (rhs == null) {
				return 1;
			}

			final int retVal = Util.compareString(lhs.name, rhs.name);
			if (retVal == 0) {
				// if name is the same compare by simlarId
				return Util.compareString(lhs.simlarId, rhs.simlarId);
			}

			return retVal;
		}
	}

	private static class RowViewHolder
	{
		public RowViewHolder(final View rowView)
		{
			this.letterView = (TextView) rowView.findViewById(R.id.letter);
			this.dividerLineView = rowView.findViewById(R.id.dividerLine);
			this.nameView = (TextView) rowView.findViewById(R.id.name);
			this.numberView = (TextView) rowView.findViewById(R.id.number);
		}

		public final TextView letterView;
		public final View dividerLineView;
		public final TextView nameView;
		public final TextView numberView;
	}

	public ContactsAdapter(final Context context)
	{
		super(context, R.layout.fragment_contacts_list_element, new ArrayList<ContactDataComplete>());
		mLayout = R.layout.fragment_contacts_list_element;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{
		final View rowView;
		final RowViewHolder holder;
		if (convertView == null) {
			rowView = mInflater.inflate(mLayout, parent, false);
			if (rowView == null) {
				Lg.e("no row view found");
				return null;
			}
			holder = new RowViewHolder(rowView);
			rowView.setTag(holder);
		} else {
			rowView = convertView;
			holder = (RowViewHolder) rowView.getTag();
		}

		final ContactDataComplete contact = getItem(position);
		if (contact == null) {
			return rowView;
		}

		if (position > 0) {
			final ContactDataComplete prevContact = getItem(position - 1);
			if (contact.getNameOrNumber().charAt(0) != prevContact.getNameOrNumber().charAt(0)) {
				holder.letterView.setVisibility(View.VISIBLE);
				holder.letterView.setText(Character.toString(contact.getNameOrNumber().charAt(0)));
				holder.dividerLineView.setVisibility(View.GONE);
			} else {
				holder.letterView.setVisibility(View.GONE);
				holder.dividerLineView.setVisibility(View.VISIBLE);
			}
		} else {
			holder.letterView.setVisibility(View.VISIBLE);
			holder.letterView.setText(Character.toString(contact.getNameOrNumber().charAt(0)));
			holder.dividerLineView.setVisibility(View.GONE);
		}

		holder.nameView.setText(contact.getNameOrNumber());
		holder.numberView.setText(contact.guiTelephoneNumber);

		return rowView;
	}

	public void addAllContacts(Set<ContactDataComplete> contacts)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.addAll(contacts);
		} else {
			for (final ContactDataComplete contact : contacts) {
				super.add(contact);
			}
		}
		sort(new SortByName());
	}

	@Override
	public void add(final ContactDataComplete contact)
	{
		super.add(contact);
		sort(new SortByName());
	}
}
